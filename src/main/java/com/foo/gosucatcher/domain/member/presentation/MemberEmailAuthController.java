package com.foo.gosucatcher.domain.member.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foo.gosucatcher.domain.member.application.MemberEmailAuthService;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberEmailAuthRequest;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberEmailAuthResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberEmailSendResponse;
import com.foo.gosucatcher.domain.member.domain.MemberEmailRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "MemberEmailAuthController", description = "인증관련 이메일 발송, 증명 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/members/auth/email")
public class MemberEmailAuthController {

	private final MemberEmailAuthService memberEmailAuthService;

	@PostMapping
	@Operation(summary = "등록된 이메일로 인증번호를 전송", description = "등록된 이메일로 인증번호를 전송합니다.")
	public ResponseEntity<MemberEmailSendResponse> sendAuthEmail(
		@RequestBody @Validated
		@Parameter(description = "이메일 인증에 필요한 정보", required = true)
		MemberEmailRequest memberEmailRequest
	) {
		memberEmailAuthService.checkDuplicatedEmail(memberEmailRequest);
		MemberEmailSendResponse authenticateResponse = memberEmailAuthService.sendAuthEmail(memberEmailRequest);

		return ResponseEntity.ok(authenticateResponse);
	}

	@PostMapping("/validation")
	@Operation(summary = "등록된 이메일로 발송된 번호 인증", description = "등록된 이메일로 발송된 번호를 인증합니다.")
	public ResponseEntity<MemberEmailAuthResponse> authenticateMemberByEmail(
		@RequestParam
		@Parameter(description = "인증에 필요한 이메일", required = true)
		String email,
		@RequestBody @Validated
		@Parameter(description = "인증에 필요한 번호", required = true)
		MemberEmailAuthRequest memberEmailAuthRequest
	) {
		MemberEmailAuthResponse memberEmailAuthResponse = memberEmailAuthService.authenticateMemberByEmail(email,
			memberEmailAuthRequest);

		return ResponseEntity.ok(memberEmailAuthResponse);
	}
}
