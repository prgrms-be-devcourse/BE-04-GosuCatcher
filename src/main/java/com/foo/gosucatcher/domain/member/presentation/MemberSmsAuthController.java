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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "MemberSmsAuthController", description = "SMS 전송 및 인증")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/members/auth/sms")
public class MemberSmsAuthController {

	private final MemberSmsAuthService memberSmsService;

	@CurrentMemberId
	@PostMapping
	@Operation(summary = "SMS를 전송", description = "인증번호 SMS를 전송합니다.")
	public ResponseEntity<SmsSendResponse> sendAuthSms(
		@Parameter(description = "토큰에서 가져온 멤버ID", required = true)
		Long memberId,
		@RequestBody @Validated
		@Parameter(description = "SMS 전송에 필요한 요청 정보", required = true)
		SmsSendRequest smsSendRequest
	) {
		SmsSendResponse smsAuthResponse = memberSmsService.sendSms(memberId, smsSendRequest);

		return ResponseEntity.ok(smsAuthResponse);
	}

	@CurrentMemberId
	@PostMapping("/validation")
	@Operation(summary = "SMS 번호인증", description = "SMS로 전송된 번호를 인증 합니다.")
	public ResponseEntity<SmsAuthResponse> authenticateSms(
		@RequestBody @Validated
		@Parameter(description = "SMS인증 완료에 필요한 정보", required = true)
		SmsAuthRequest smsAuthRequest
	) {
		SmsAuthResponse smsAuthResponse = memberSmsService.authenticateSms(smsAuthRequest);

		return ResponseEntity.ok(smsAuthResponse);
	}
}
