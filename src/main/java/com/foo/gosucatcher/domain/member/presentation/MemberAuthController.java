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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "회원 관련 Controller",
	description = "회원가입, 로그인, 로그아웃, 회원탈퇴, 비밀번호 찾기, 토큰재발급 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/members")
public class MemberAuthController {

	private final MemberAuthService memberAuthService;

	@PostMapping("/signup")
	@Operation(summary = "회원가입 요청", description = "회원가입이 됩니다.")
	public ResponseEntity<MemberSignupResponse> signup(
		@RequestBody @Validated
		@Parameter(description = "회원가입에 필요한 정보", required = true)
		MemberSignupRequest memberSignUpRequest
	) {
		MemberSignupResponse response = memberAuthService.signup(memberSignUpRequest);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(response);
	}

	@PostMapping("/login")
	@Operation(summary = "회원 로그인", description = "로그인 할 수 있습니다.")
	public ResponseEntity<MemberCertifiedResponse> login(
		@RequestBody @Validated
		@Parameter(description = "로그인에 필요한 정보", required = true)
		MemberLoginRequest memberLoginRequest) {
		MemberCertifiedResponse response = memberAuthService.login(memberLoginRequest);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/reissue")
	@Operation(summary = "토큰 재발급", description = "액세스 토큰이 만료되면 재발급 할 수 있습니다.")
	public ResponseEntity<JwtReissueResponse> reissue(
		@RequestHeader(value = "RefreshToken", required = false)
		@Parameter(description = "액세트 토큰 발급에 필요한 리플래시 토큰", required = true)
		String refreshToken
	) {
		if (refreshToken == null) {
			throw new HttpHeaderException(ErrorCode.NOT_FOUND_REFRESH_TOKEN);
		}

		JwtReissueResponse response = memberAuthService.reissue(refreshToken);

		return ResponseEntity.ok(response);
	}

	@CurrentMemberEmail
	@DeleteMapping("/logout")
	@Operation(summary = "로그아웃", description = "토큰에서 이메일 정보가 인증되면 로그아웃됩니다.")
	public ResponseEntity<Void> logout(
		@Parameter(description = "토큰에서 파싱된 이메일 정보", required = true)
		String memberEmail
	) {
		memberAuthService.logout(memberEmail);

		return ResponseEntity.noContent().build();
	}

	@CurrentMemberId
	@DeleteMapping
	@Operation(summary = "회원탈퇴", description = "토큰에서 멤버ID를 파싱해서 회원탈퇴합니다.")
	public ResponseEntity<Void> deleteMember(
		@Parameter(description = "토큰에서 파싱된 멤버ID", required = true)
		Long memberId
	) {
		memberAuthService.deleteMember(memberId);

		return ResponseEntity.noContent().build();
	}

	@PostMapping("/recovery/password")
	@Operation(summary = "비밀번호 찾기", description = "회원이름과 이메일을 검증하고 등록된 이메일로 임시비밀번호를 발급합니다.")
	public ResponseEntity<MemberPasswordFoundResponse> findPassword(
		@RequestBody @Validated
		@Parameter(description = "비밀번호 찾기에 필요한 요청 정보", required = true)
		MemberPasswordFoundRequest memberPasswordFoundRequest
	) {
		MemberPasswordFoundResponse response = memberAuthService.findPassword(memberPasswordFoundRequest);

		return ResponseEntity.ok(response);
	}
}
