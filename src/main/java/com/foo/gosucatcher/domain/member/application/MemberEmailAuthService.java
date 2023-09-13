package com.foo.gosucatcher.domain.member.application;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foo.gosucatcher.config.EmailAuthProperties;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberEmailAuthRequest;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberEmailAuthResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberEmailSendResponse;
import com.foo.gosucatcher.domain.member.domain.Email;
import com.foo.gosucatcher.domain.member.domain.MemberRepository;
import com.foo.gosucatcher.domain.member.exception.EmailAuthException;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.util.EmailRedisTemplateUtils;
import com.foo.gosucatcher.global.util.RandomNumberUtils;

@Transactional
@Service
public class MemberEmailAuthService {

	private static final String AUTH_EMAIL_SUBJECT = "고수캐쳐(GoSu-Catcher) 이메일 인증";
	private static final String AUTH_EMAIL_BODY = """
		<h3>요청하신 인증 번호입니다</h3>
		<h1>%s</h1>
		<h3>감사합니다.</h3>
		""";
	private static final String RECOVERY_PASSWORD_EMAIL_SUBJECT = "임시 비밀번호 발급";
	private static final String RECOVERY_PASSWORD_EMAIL_BODY = """
		<h3>임시 비밀번호</h3>
		<h1>%s</h1>
		<h3>로그인 후 꼭 비밀번호를 변경해주세요! 감사합니다.</h3>
		""";

	private final EmailRedisTemplateUtils emailRedisTemplateUtils;
	private final JavaMailSender javaMailSender;
	private final MemberRepository memberRepository;

	private final String SENDER_EMAIL;
	private final Long EXPIRATION_TIME;

	public MemberEmailAuthService(EmailRedisTemplateUtils emailRedisTemplateUtils, JavaMailSender javaMailSender,
		MemberRepository memberRepository, EmailAuthProperties emailAuthProperties) {
		this.emailRedisTemplateUtils = emailRedisTemplateUtils;
		this.javaMailSender = javaMailSender;
		this.memberRepository = memberRepository;
		this.SENDER_EMAIL = emailAuthProperties.getSenderEmail();
		this.EXPIRATION_TIME = emailAuthProperties.getExpirationTime();
	}

	@Transactional(readOnly = true)
	public void checkDuplicatedEmail(Email email) {
		String requestEmail = email.getEmail();
		if (memberRepository.existsByEmail(requestEmail)) {
			throw new EmailAuthException(ErrorCode.DUPLICATED_MEMBER);
		}
	}

	public MemberEmailSendResponse sendAuthEmail(Email email) {
		String authNumber = RandomNumberUtils.createAuthenticationStringNumber();
		String requestEmail = email.getEmail();
		MimeMessage message = createAuthenticationEmail(authNumber, requestEmail);
		javaMailSender.send(message);

		emailRedisTemplateUtils.put(requestEmail, authNumber, EXPIRATION_TIME);

		return MemberEmailSendResponse.from(message, EXPIRATION_TIME);
	}

	public MemberEmailAuthResponse authenticateMemberByEmail(String email,
		MemberEmailAuthRequest memberEmailAuthRequest) {
		String authNumber = emailRedisTemplateUtils.get(email, String.class);

		if (authNumber == null) {
			throw new EmailAuthException(ErrorCode.INVALID_AUTH);
		}

		String requestAuthNumber = memberEmailAuthRequest.authNumber();
		if (!authNumber.equals(requestAuthNumber)) {
			throw new EmailAuthException(ErrorCode.INCORRECT_AUTH_NUMBER);
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
			String body = AUTH_EMAIL_BODY.formatted(authNumber);
			message.setFrom(SENDER_EMAIL);
			message.setRecipients(MimeMessage.RecipientType.TO, email);
			message.setSubject(AUTH_EMAIL_SUBJECT);
			message.setText(body, "UTF-8", "html");
		} catch (MessagingException e) {
			throw new EmailAuthException(ErrorCode.NOT_CREATION_AUTH_MESSAGE);
		}

		return message;
	}

	private MimeMessage createRecoveryEmail(String temporaryPassword, String email) {
		MimeMessage message = javaMailSender.createMimeMessage();

		try {
			String body = RECOVERY_PASSWORD_EMAIL_BODY.formatted(temporaryPassword);
			message.setFrom(SENDER_EMAIL);
			message.setRecipients(MimeMessage.RecipientType.TO, email);
			message.setSubject(RECOVERY_PASSWORD_EMAIL_SUBJECT);
			message.setText(body, "UTF-8", "html");
		} catch (MessagingException e) {
			throw new EmailAuthException(ErrorCode.NOT_CREATION_AUTH_MESSAGE);
		}

		return message;
	}
}
