package com.foo.gosucatcher.domain.member.application;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foo.gosucatcher.domain.member.application.dto.request.SmsAuthRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.SmsSendRequest;
import com.foo.gosucatcher.domain.member.application.dto.response.SmsAuthResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.SmsSendResponse;
import com.foo.gosucatcher.domain.member.domain.MemberRepository;
import com.foo.gosucatcher.domain.member.exception.SmsAuthException;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.util.RandomNumberUtils;
import com.foo.gosucatcher.global.util.SmsRedisTemplateUtils;

import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.FailedMessage;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;

@Slf4j
@Transactional
@Service
public class MemberSmsAuthService {
	@Value("${auth.phone.fromNumber}")
	private String FROM_NUMBER;
	private static final String TEXT_MESSAGE = """
		고수캐처(Gosu-Catcher)
		인증번호: %s
		""";

	private final SmsRedisTemplateUtils smsRedisTemplateUtils;
	private final DefaultMessageService messageService;

	public MemberSmsAuthService(SmsRedisTemplateUtils smsRedisTemplateUtils, MemberRepository memberRepository,
		@Value("${secret.coolsms.apiKey}") String apiKey, @Value("${secret.coolsms.apiSecret}") String apiSecret,
		@Value("${secret.coolsms.domain}") String domain) {
		this.smsRedisTemplateUtils = smsRedisTemplateUtils;
		this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, domain);
	}

	public SmsSendResponse sendSms(Long memberId, SmsSendRequest smsSendRequest) {
		String toNumber = smsSendRequest.phoneNumber();
		String authNumber = RandomNumberUtils.createAuthenticationStringNumber();

		Message message = new Message();
		message.setFrom(FROM_NUMBER);
		message.setTo(toNumber);
		message.setText(TEXT_MESSAGE.formatted(authNumber));

		try {
			messageService.send(message);
		} catch (NurigoMessageNotReceivedException exception) {
			List<FailedMessage> failedMessageList = exception.getFailedMessageList();
			failedMessageList.forEach(msg -> log.warn(msg.toString()));
			log.warn(exception.getMessage());
		} catch (Exception exception) {
			log.warn(exception.getMessage());
		}

		smsRedisTemplateUtils.put(toNumber, authNumber, 60 * 10L);

		return SmsSendResponse.from(memberId, toNumber);
	}

	public SmsAuthResponse authenticateSms(SmsAuthRequest smsAuthRequest) {
		String phoneNumber = smsAuthRequest.phoneNumber();
		String authNumber = smsRedisTemplateUtils.get(phoneNumber, String.class);

		if (authNumber == null) {
			throw new SmsAuthException(ErrorCode.INVALID_AUTH);
		}

		String requestAuthNumber = smsAuthRequest.authNumber();
		if (!authNumber.equals(requestAuthNumber)) {
			throw new SmsAuthException(ErrorCode.INCORRECT_AUTH_NUMBER);
		}
		smsRedisTemplateUtils.delete(phoneNumber);

		return new SmsAuthResponse(phoneNumber, true);
	}
}
