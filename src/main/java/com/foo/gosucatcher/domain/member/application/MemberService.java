package com.foo.gosucatcher.domain.member.application;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import com.foo.gosucatcher.domain.member.application.dto.request.MemberEmailAuthRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberLoginRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberPasswordFoundRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberProfileChangeRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberRefreshRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberSignupRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.ProfileImageUploadRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.SmsAuthRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.SmsSendRequest;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberCertifiedResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberEmailAuthResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberEmailSendResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberPasswordFoundResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberProfileChangeResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberProfileResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberSignupResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.SmsAuthResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.SmsSendResponse;
import com.foo.gosucatcher.domain.member.domain.ImageFile;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.domain.member.domain.MemberProfileRepository;
import com.foo.gosucatcher.domain.member.domain.MemberRepository;
import com.foo.gosucatcher.domain.member.domain.Roles;
import com.foo.gosucatcher.domain.member.exception.MemberCertifiedFailException;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.BusinessException;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;
import com.foo.gosucatcher.global.error.exception.InvalidValueException;
import com.foo.gosucatcher.global.security.JwtTokenProvider;
import com.foo.gosucatcher.global.util.RedisUtils;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;

@Transactional
@Service
public class MemberService {

	private final MemberRepository memberRepository;
	private final MemberProfileRepository memberProfileRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final PasswordEncoder passwordEncoder;
	private final JavaMailSender javaMailSender;
	private final RedisUtils redisUtil;
	private final DefaultMessageService defaultMessageService;
	private static final String SENDER_EMAIL = "ysngisyosong@gmail.com";
	private static final long EXPIRATION_TIME = 60 * 5L;
	private String AUTH_NUMBER;
	private final String FROM_NUMBER;

	public MemberService(
		MemberRepository memberRepository,
		MemberProfileRepository memberProfileRepository,
		JwtTokenProvider jwtTokenProvider,
		PasswordEncoder passwordEncoder,
		JavaMailSender javaMailSender,
		RedisUtils redisUtil,
		@Value("${secret.coolsms.apiKey}")
		String apiKey,
		@Value("${secret.coolsms.apiSecret}")
		String apiSecret,
		@Value("${secret.coolsms.fromNumber}")
		String fromNumber
	) {
		this.memberRepository = memberRepository;
		this.memberProfileRepository = memberProfileRepository;
		this.jwtTokenProvider = jwtTokenProvider;
		this.passwordEncoder = passwordEncoder;
		this.javaMailSender = javaMailSender;
		this.redisUtil = redisUtil;
		this.FROM_NUMBER = fromNumber;
		this.defaultMessageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
	}

	public MemberEmailSendResponse sendAuthEmail(String email) {
		MimeMessage message = createAuthenticationEmail(email);
		javaMailSender.send(message);

		redisUtil.put(email, AUTH_NUMBER, EXPIRATION_TIME);

		return MemberEmailSendResponse.from(message, EXPIRATION_TIME);
	}

	public MemberEmailAuthResponse authenticateMemberByEmail(MemberEmailAuthRequest memberEmailAuthRequest) {
		String email = memberEmailAuthRequest.email();
		String authNumber = redisUtil.get(email, String.class);

		if (authNumber == null) {
			throw new BusinessException(ErrorCode.EXPIRED_AUTH_NUMBER);
		}

		String requestAuthNumber = memberEmailAuthRequest.authNumber();
		if (!authNumber.equals(requestAuthNumber)) {
			throw new InvalidValueException(ErrorCode.INCORRECT_AUTH_NUMBER);
		}
		redisUtil.delete(email);

		return new MemberEmailAuthResponse(email, true);
	}

	public MemberPasswordFoundResponse findPassword(MemberPasswordFoundRequest memberPasswordFoundRequest) {
		String requestEmail = memberPasswordFoundRequest.email();
		Member member = memberRepository.findByEmail(requestEmail)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		String requestName = memberPasswordFoundRequest.name();
		member.isSameMemberName(requestName);

		String temporaryPassword = createTemporaryPassword();
		member.changePassword(temporaryPassword, passwordEncoder);
		MimeMessage recoveryEmail = createRecoveryEmail(temporaryPassword, requestEmail);
		javaMailSender.send(recoveryEmail);

		return MemberPasswordFoundResponse.from(member);
	}

	@Transactional(readOnly = true)
	public void checkDuplicatedEmail(String email) {
		memberRepository.findByEmail(email)
			.ifPresent((member) -> {
				throw new InvalidValueException(ErrorCode.DUPLICATED_MEMBER);
			});
	}

	public MemberSignupResponse signup(MemberSignupRequest memberSignUpRequest) {
		Member signupMember = MemberSignupRequest.toMember(memberSignUpRequest);
		String signupEmail = signupMember.getEmail();

		checkDuplicatedEmail(signupEmail);

		String password = signupMember.getPassword();
		signupMember.changePassword(password, passwordEncoder);
		signupMember.updateMemberRole(Roles.ROLE_USER);
		Member savedMember = memberRepository.save(signupMember);

		memberProfileRepository.initializeMemberProfile(savedMember);

		return MemberSignupResponse.from(savedMember);
	}

	@Transactional
	public MemberCertifiedResponse login(MemberLoginRequest memberLoginRequest) {
		String loginRequestEmail = memberLoginRequest.email();
		Member member = memberRepository.findByEmail(loginRequestEmail)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		Member loginRequestMember = MemberLoginRequest.toMember(memberLoginRequest);
		member.authenticate(loginRequestMember, passwordEncoder);

		return getMemberCertifiedResponse(member);
	}

	public MemberCertifiedResponse refresh(MemberRefreshRequest memberRefreshRequest) {
		String refreshToken = memberRefreshRequest.refreshToken();

		if (jwtTokenProvider.isValidRefreshToken(refreshToken)) {
			throw new MemberCertifiedFailException(ErrorCode.CERTIFICATION_FAIL);
		}

		Member member = getMemberByRefreshToken(refreshToken);

		String memberRefreshToken = member.getRefreshToken();
		if (!memberRefreshToken.equals(refreshToken)) {
			throw new MemberCertifiedFailException(ErrorCode.CERTIFICATION_FAIL);
		}

		return getMemberCertifiedResponse(member);
	}

	public void logout(String memberEmail) {
		Member member = memberRepository.findByEmail(memberEmail)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		member.logout();
	}

	public void deleteMember(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		memberRepository.delete(member);
	}

	public MemberProfileResponse findMemberProfile(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		return MemberProfileResponse.from(member);
	}

	public MemberProfileChangeResponse changeMemberProfile(Long memberId,
		@Validated MemberProfileChangeRequest memberProfileChangeRequest) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		Member changedMember = MemberProfileChangeRequest.toMember(memberProfileChangeRequest);
		member.updateProfile(changedMember, passwordEncoder);

		return MemberProfileChangeResponse.from(member);
	}

	public void uploadProfileImage(ProfileImageUploadRequest profileImageUploadRequest) {
		long memberId = profileImageUploadRequest.memberId();
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		MultipartFile file = profileImageUploadRequest.file();
		ImageFile imageFile = memberProfileRepository.uploadImage(member, file);

		member.updateProfileImage(imageFile);
	}

	@Transactional(readOnly = true)
	public ImageFile findProfileImage(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		return memberProfileRepository.findImage(member);
	}

	public void deleteProfileImage(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		ImageFile profileImageFile = member.getProfileImageFile();
		memberProfileRepository.deleteImage(profileImageFile);
	}

	private MemberCertifiedResponse getMemberCertifiedResponse(Member member) {
		String memberEmail = member.getEmail();
		Long memberId = member.getId();

		String accessToken = jwtTokenProvider.createAccessToken(memberEmail, memberId);
		String refreshToken = jwtTokenProvider.createRefreshToken(memberEmail, memberId);

		member.refreshToken(refreshToken);

		return MemberCertifiedResponse.from(accessToken, refreshToken);
	}

	private Member getMemberByRefreshToken(String refreshToken) {
		Authentication authentication = jwtTokenProvider.getRefreshTokenAuthentication(refreshToken);
		String memberEmail = authentication.getName();

		return memberRepository.findByEmail(memberEmail)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));
	}

	private void createAuthenticationNumber() {
		AUTH_NUMBER = String.valueOf((int)(Math.random() * (90_000)) + 100_000);
	}

	private MimeMessage createAuthenticationEmail(String email) {
		createAuthenticationNumber();
		MimeMessage message = javaMailSender.createMimeMessage();

		try {
			message.setFrom(SENDER_EMAIL);
			message.setRecipients(MimeMessage.RecipientType.TO, email);
			message.setSubject("이메일 인증");

			String body = """
				<h3>요청하신 인증 번호입니다</h3>
				<h1>%s</h1>
				<h3>감사합니다.</h3>
				""".formatted(AUTH_NUMBER);
			message.setText(body, "UTF-8", "html");
		} catch (MessagingException e) {
			throw new RuntimeException("이메일 발송 실패임");
		}

		return message;
	}

	private String createTemporaryPassword() {
		return String.valueOf((int)(Math.random() * (9_000_000)) + 10_000_000);
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
			throw new RuntimeException("이메일 발송 실패임");
		}

		return message;
	}

	public SmsSendResponse sendSms(Long memberId, SmsSendRequest smsSendRequest) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		String toNumber = smsSendRequest.phoneNumber();
		createAuthenticationNumber();

		Message message = new Message();
		message.setFrom(FROM_NUMBER);
		message.setTo(toNumber);
		message.setText("""
			[Web 발신] 고수캐처(Gosu-Catcher)
			인증번호: %s
			""".formatted(AUTH_NUMBER));

		try {
			defaultMessageService.send(message);
		} catch (NurigoMessageNotReceivedException exception) {
			System.out.println(exception.getFailedMessageList());
			System.out.println(exception.getMessage());
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}

		redisUtil.put(toNumber, AUTH_NUMBER, 60 * 10L);

		return SmsSendResponse.from(member, toNumber);
	}

	public SmsAuthResponse authenticateSms(Long memberId, SmsAuthRequest smsAuthRequest) {
		memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		String phoneNumber = smsAuthRequest.phoneNumber();
		String authNumber = redisUtil.get(phoneNumber, String.class);

		if (authNumber == null) {
			throw new BusinessException(ErrorCode.EXPIRED_AUTH_NUMBER);
		}

		String requestAuthNumber = smsAuthRequest.authNumber();
		if (!authNumber.equals(requestAuthNumber)) {
			throw new InvalidValueException(ErrorCode.INCORRECT_AUTH_NUMBER);
		}
		redisUtil.delete(phoneNumber);

		return new SmsAuthResponse(phoneNumber, true);
	}
}
