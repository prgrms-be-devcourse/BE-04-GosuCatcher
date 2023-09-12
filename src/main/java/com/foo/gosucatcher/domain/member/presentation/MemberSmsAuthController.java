package com.foo.gosucatcher.domain.member.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foo.gosucatcher.domain.member.application.MemberSmsAuthService;
import com.foo.gosucatcher.domain.member.application.dto.request.SmsAuthRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.SmsSendRequest;
import com.foo.gosucatcher.domain.member.application.dto.response.SmsAuthResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.SmsSendResponse;
import com.foo.gosucatcher.global.aop.CurrentMemberId;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/members/auth/sms")
public class MemberSmsAuthController {

	private final MemberSmsAuthService memberSmsService;

	@CurrentMemberId
	@PostMapping
	public ResponseEntity<SmsSendResponse> sendAuthSms(Long memberId,
		@RequestBody @Validated SmsSendRequest smsSendRequest) {
		SmsSendResponse smsAuthResponse = memberSmsService.sendSms(memberId, smsSendRequest);

		return ResponseEntity.ok(smsAuthResponse);
	}

	@CurrentMemberId
	@PostMapping("/validation")
	public ResponseEntity<SmsAuthResponse> authenticateSms(Long memberId,
		@RequestBody @Validated SmsAuthRequest smsAuthRequest) {
		SmsAuthResponse smsAuthResponse = memberSmsService.authenticateSms(memberId, smsAuthRequest);

		return ResponseEntity.ok(smsAuthResponse);
	}
}
