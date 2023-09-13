package com.foo.gosucatcher.domain.member.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foo.gosucatcher.config.SmsAuthProperties;
import com.foo.gosucatcher.domain.member.application.dto.request.SmsAuthRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.SmsSendRequest;
import com.foo.gosucatcher.domain.member.application.dto.response.SmsAuthResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.SmsSendResponse;
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

	private static final String TEXT_MESSAGE = """
		고수캐처(Gosu-Catcher)
		인증번호: %s
		""";

	private final SmsRedisTemplateUtils smsRedisTemplateUtils;
	private final DefaultMessageService messageService;

	private final String FROM_NUMBER;
	private final Long EXPIRATION_TIME;

	public MemberSmsAuthService(SmsRedisTemplateUtils smsRedisTemplateUtils, SmsAuthProperties smsAuthProperties) {
		this.smsRedisTemplateUtils = smsRedisTemplateUtils;
		this.messageService = NurigoApp.INSTANCE.initialize(smsAuthProperties.getApiKey(),
			smsAuthProperties.getApiKey(), smsAuthProperties.getDomain());
		this.FROM_NUMBER = smsAuthProperties.getFromNumber();
		this.EXPIRATION_TIME = smsAuthProperties.getExpirationTime();
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

		smsRedisTemplateUtils.put(toNumber, authNumber, EXPIRATION_TIME);

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
