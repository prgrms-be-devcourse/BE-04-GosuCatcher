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
import com.foo.gosucatcher.domain.member.domain.Email;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/members/auth/email")
public class MemberEmailAuthController {

	private final MemberEmailAuthService memberEmailAuthService;

	@PostMapping
	public ResponseEntity<MemberEmailSendResponse> sendAuthEmail(@RequestBody @Validated Email email) {
		memberEmailAuthService.checkDuplicatedEmail(email);
		MemberEmailSendResponse authenticateResponse = memberEmailAuthService.sendAuthEmail(email);

		return ResponseEntity.ok(authenticateResponse);
	}

	@PostMapping("/validation")
	public ResponseEntity<MemberEmailAuthResponse> authenticateMemberByEmail(@RequestParam String email,
		@RequestBody @Validated MemberEmailAuthRequest memberEmailAuthRequest) {
		MemberEmailAuthResponse memberEmailAuthResponse = memberEmailAuthService.authenticateMemberByEmail(email,
			memberEmailAuthRequest);

		return ResponseEntity.ok(memberEmailAuthResponse);
	}
}
