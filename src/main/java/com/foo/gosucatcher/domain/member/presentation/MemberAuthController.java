package com.foo.gosucatcher.domain.member.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foo.gosucatcher.domain.member.application.MemberAuthService;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberLoginRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberPasswordFoundRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberSignupRequest;
import com.foo.gosucatcher.domain.member.application.dto.response.JwtReissueResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberCertifiedResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberPasswordFoundResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberSignupResponse;
import com.foo.gosucatcher.global.aop.CurrentMemberEmail;
import com.foo.gosucatcher.global.aop.CurrentMemberId;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.HttpHeaderException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/members")
public class MemberAuthController {

	private final MemberAuthService memberAuthService;

	@PostMapping("/signup")
	public ResponseEntity<MemberSignupResponse> signup(
		@RequestBody @Validated MemberSignupRequest memberSignUpRequest) {
		MemberSignupResponse response = memberAuthService.signup(memberSignUpRequest);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(response);
	}

	@PostMapping("/login")
	public ResponseEntity<MemberCertifiedResponse> login(
		@RequestBody @Validated MemberLoginRequest memberLoginRequest) {
		MemberCertifiedResponse response = memberAuthService.login(memberLoginRequest);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/reissue")
	public ResponseEntity<JwtReissueResponse> reissue(
		@RequestHeader(value = "RefreshToken", required = false) String refreshToken) {
		if (refreshToken == null) {
			throw new HttpHeaderException(ErrorCode.NOT_FOUND_REFRESH_TOKEN);
		}

		JwtReissueResponse response = memberAuthService.reissue(refreshToken);

		return ResponseEntity.ok(response);
	}

	@CurrentMemberEmail
	@DeleteMapping("/logout")
	public ResponseEntity<Void> logout(String memberEmail) {
		memberAuthService.logout(memberEmail);

		return ResponseEntity.noContent().build();
	}

	@CurrentMemberId
	@DeleteMapping
	public ResponseEntity<Void> deleteMember(Long memberId) {
		memberAuthService.deleteMember(memberId);

		return ResponseEntity.noContent().build();
	}

	@PostMapping("/recovery/password")
	public ResponseEntity<MemberPasswordFoundResponse> findPassword(
		@RequestBody @Validated MemberPasswordFoundRequest memberPasswordFoundRequest) {
		MemberPasswordFoundResponse response = memberAuthService.findPassword(memberPasswordFoundRequest);

		return ResponseEntity.ok(response);
	}
}
