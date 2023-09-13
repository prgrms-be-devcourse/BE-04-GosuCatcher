package com.foo.gosucatcher.domain.member.application;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foo.gosucatcher.domain.member.application.dto.request.MemberEmailAuthRequest;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberEmailAuthResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberEmailSendResponse;
import com.foo.gosucatcher.domain.member.domain.MemberRepository;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.BusinessException;
import com.foo.gosucatcher.global.error.exception.InvalidValueException;
import com.foo.gosucatcher.global.util.EmailRedisTemplateUtils;
import com.foo.gosucatcher.global.util.RandomNumberUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class MemberEmailAuthService {

	private final EmailRedisTemplateUtils emailRedisTemplateUtils;
	private final JavaMailSender javaMailSender;
	private final MemberRepository memberRepository;

	@Value("${auth.email.senderEmail}")
	private String SENDER_EMAIL;
	@Value("${auth.time.expiration}")
	private Long EXPIRATION_TIME;

	@Transactional(readOnly = true)
	public void checkDuplicatedEmail(String email) {
		if (memberRepository.existsByEmail(email)) {
			throw new InvalidValueException(ErrorCode.DUPLICATED_MEMBER);
		}
	}

	public MemberEmailSendResponse sendAuthEmail(String email) {
		String authNumber = RandomNumberUtils.createAuthenticationStringNumber();
		MimeMessage message = createAuthenticationEmail(authNumber, email);
		javaMailSender.send(message);

		emailRedisTemplateUtils.put(email, authNumber, EXPIRATION_TIME);

		return MemberEmailSendResponse.from(message, EXPIRATION_TIME);
	}

	public MemberEmailAuthResponse authenticateMemberByEmail(MemberEmailAuthRequest memberEmailAuthRequest) {
		String email = memberEmailAuthRequest.email();
		String authNumber = emailRedisTemplateUtils.get(email, String.class);

		if (authNumber == null) {
			throw new BusinessException(ErrorCode.EXPIRED_AUTH_NUMBER);
		}

		String requestAuthNumber = memberEmailAuthRequest.authNumber();
		if (!authNumber.equals(requestAuthNumber)) {
			throw new InvalidValueException(ErrorCode.INCORRECT_AUTH_NUMBER);
		}
		emailRedisTemplateUtils.delete(email);

		return new MemberEmailAuthResponse(email, true);
	}

	public void sendPasswordRecoveryEmail(String temporaryPassword, String email) {
		MimeMessage recoveryEmail = createRecoveryEmail(temporaryPassword, email);
		javaMailSender.send(recoveryEmail);
	}

	private MimeMessage createAuthenticationEmail(String authNumber, String email) {
		MimeMessage message = javaMailSender.createMimeMessage();

		try {
			message.setFrom(SENDER_EMAIL);
			message.setRecipients(MimeMessage.RecipientType.TO, email);
			message.setSubject("이메일 인증");

			String body = """
				<h3>요청하신 인증 번호입니다</h3>
				<h1>%s</h1>
				<h3>감사합니다.</h3>
				""".formatted(authNumber);
			message.setText(body, "UTF-8", "html");
		} catch (MessagingException e) {
			throw new BusinessException(ErrorCode.NOT_CREATION_AUTH_MESSAGE);
		}

		return message;
	}

	private MimeMessage createRecoveryEmail(String temporaryPassword, String email) {
		MimeMessage message = javaMailSender.createMimeMessage();

		try {
			message.setFrom(SENDER_EMAIL);
			message.setRecipients(MimeMessage.RecipientType.TO, email);
			message.setSubject("임시 비밀번호 발급");

			String body = """
				<h3>임시 비밀번호</h3>
				<h1>%s</h1>
				<h3>로그인 후 꼭 비밀번호를 변경해주세요! 감사합니다.</h3>
				""".formatted(temporaryPassword);
			message.setText(body, "UTF-8", "html");
		} catch (MessagingException e) {
			throw new BusinessException(ErrorCode.NOT_CREATION_AUTH_MESSAGE);
		}

		return message;
	}
}
