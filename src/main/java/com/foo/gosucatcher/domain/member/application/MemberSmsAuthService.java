package com.foo.gosucatcher.domain.member.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foo.gosucatcher.domain.member.application.dto.request.SmsAuthRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.SmsSendRequest;
import com.foo.gosucatcher.domain.member.application.dto.response.SmsAuthResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.SmsSendResponse;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.domain.member.domain.MemberRepository;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.BusinessException;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;
import com.foo.gosucatcher.global.error.exception.InvalidValueException;
import com.foo.gosucatcher.global.util.RandomNumberUtils;
import com.foo.gosucatcher.global.util.SmsRedisTemplateUtils;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;

@Transactional
@Service
public class MemberSmsAuthService {

	private final SmsRedisTemplateUtils smsRedisTemplateUtils;
	private final DefaultMessageService messageService;
	private final MemberRepository memberRepository;

	@Value("${auth.phone.fromNumber}")
	private String FROM_NUMBER;

	public MemberSmsAuthService(SmsRedisTemplateUtils smsRedisTemplateUtils, MemberRepository memberRepository,
		@Value("${secret.coolsms.apiKey}") String apiKey, @Value("${secret.coolsms.apiSecret}") String apiSecret,
		@Value("${secret.coolsms.domain}") String domain) {
		this.smsRedisTemplateUtils = smsRedisTemplateUtils;
		this.memberRepository = memberRepository;
		this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, domain);
	}

	public SmsSendResponse sendSms(Long memberId, SmsSendRequest smsSendRequest) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		String toNumber = smsSendRequest.phoneNumber();
		String authNumber = RandomNumberUtils.createAuthenticationStringNumber();

		Message message = new Message();
		message.setFrom(FROM_NUMBER);
		message.setTo(toNumber);
		message.setText("""
			고수캐처(Gosu-Catcher)
			인증번호: %s
			""".formatted(authNumber));

		try {
			messageService.send(message);
		} catch (NurigoMessageNotReceivedException exception) {
			System.out.println(exception.getFailedMessageList());
			System.out.println(exception.getMessage());
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}

		smsRedisTemplateUtils.put(toNumber, authNumber, 60 * 10L);

		return SmsSendResponse.from(member, toNumber);
	}

	public SmsAuthResponse authenticateSms(Long memberId, SmsAuthRequest smsAuthRequest) {
		memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		String phoneNumber = smsAuthRequest.phoneNumber();
		String authNumber = smsRedisTemplateUtils.get(phoneNumber, String.class);

		if (authNumber == null) {
			throw new BusinessException(ErrorCode.EXPIRED_AUTH_NUMBER);
		}

		String requestAuthNumber = smsAuthRequest.authNumber();
		if (!authNumber.equals(requestAuthNumber)) {
			throw new InvalidValueException(ErrorCode.INCORRECT_AUTH_NUMBER);
		}
		smsRedisTemplateUtils.delete(phoneNumber);

		return new SmsAuthResponse(phoneNumber, true);
	}
}
