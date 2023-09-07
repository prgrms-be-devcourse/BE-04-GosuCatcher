package com.foo.gosucatcher.domain.member.presentation;

import javax.validation.constraints.Email;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.foo.gosucatcher.domain.member.application.MemberService;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberLoginRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberProfileChangeRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberRefreshRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberSignupRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.ProfileImageUploadRequest;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberCertifiedResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberEmailDuplicateResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberPasswordFoundResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberProfileChangeResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberProfileResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberSignupResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.ProfileImageUploadResponse;
import com.foo.gosucatcher.domain.member.domain.ImageFile;
import com.foo.gosucatcher.global.aop.CurrentMemberEmail;
import com.foo.gosucatcher.global.aop.CurrentMemberId;
import com.foo.gosucatcher.global.util.ImageFileUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/members")
public class MemberController {

	private final MemberService memberService;

	@PostMapping("/recovery/password")
	public ResponseEntity<MemberPasswordFoundResponse> findPassword(
		@RequestBody @Validated @Email(message = "올바른 이메일 형식을 입력하세요") String email) {
		//todo: 이메일 인증 시스템 만들기
		MemberPasswordFoundResponse response = memberService.findPassword(email);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/signup")
	public ResponseEntity<MemberEmailDuplicateResponse> checkDuplicatedEmail(
		@RequestParam @Validated @Email(message = "올바른 이메일 형식을 입력하세요") String email) {
		MemberEmailDuplicateResponse response = memberService.checkDuplicatedEmail(email);

		return ResponseEntity.ok(response);
	}

	@PostMapping("/signup")
	public ResponseEntity<MemberSignupResponse> signup(
		@RequestBody @Validated MemberSignupRequest memberSignUpRequest) {
		MemberSignupResponse response = memberService.signup(memberSignUpRequest);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(response);
	}

	@PostMapping("/login")
	public ResponseEntity<MemberCertifiedResponse> login(
		@RequestBody @Validated MemberLoginRequest memberLoginRequest) {
		MemberCertifiedResponse response = memberService.login(memberLoginRequest);

		return ResponseEntity.ok(response);
	}

	@PostMapping("/refresh-token")
	public ResponseEntity<MemberCertifiedResponse> refreshToken(
		@RequestBody @Validated MemberRefreshRequest memberRefreshRequest) {
		MemberCertifiedResponse response = memberService.refresh(memberRefreshRequest);

		return ResponseEntity.ok(response);
	}

	@CurrentMemberEmail
	@DeleteMapping("/logout")
	public ResponseEntity<Void> logout(String memberEmail) {
		memberService.logout(memberEmail);

		return ResponseEntity.noContent().build();
	}

	@CurrentMemberId
	@DeleteMapping("/me")
	public ResponseEntity<Void> deleteMember(Long memberId) {
		memberService.deleteMember(memberId);

		return ResponseEntity.noContent().build();
	}

	@CurrentMemberId
	@GetMapping("/me/profile")
	public ResponseEntity<MemberProfileResponse> findMemberProfile(Long memberId) {
		MemberProfileResponse response = memberService.findMemberProfile(memberId);

		return ResponseEntity.ok(response);
	}

	@CurrentMemberId
	@PatchMapping("/me/profile")
	public ResponseEntity<MemberProfileChangeResponse> changeMemberProfile(Long memberId,
		@RequestBody @Validated MemberProfileChangeRequest memberProfileChangeRequest) {
		MemberProfileChangeResponse response = memberService.changeMemberProfile(memberId,
			memberProfileChangeRequest);

		return ResponseEntity.ok(response);
	}

	//todo: 리팩토링 예정
	@PostMapping("/me/profile/image")
	public ResponseEntity<ProfileImageUploadResponse> uploadProfileImage(@PathVariable long memberId,
		@RequestParam MultipartFile file) {
		ProfileImageUploadRequest request = new ProfileImageUploadRequest(memberId, file);
		memberService.uploadProfileImage(request);

		ProfileImageUploadResponse response = new ProfileImageUploadResponse(memberId, file.getOriginalFilename());

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(response);
	}

	@GetMapping("/me/profile/image")
	public ResponseEntity<Resource> findProfileImage(@PathVariable long memberId) {
		ImageFile profileImage = memberService.findProfileImage(memberId);

		String contentDisposition = ImageFileUtils.makeImageFileContentDisposition(profileImage);
		UrlResource resource = ImageFileUtils.makeImageFileUrlResource(profileImage);

		return ResponseEntity.status(HttpStatus.OK)
			.header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
			.body(resource);
	}

	@DeleteMapping("/me/profile/image")
	public ResponseEntity<Void> deleteProfileImage(@PathVariable long memberId) {
		memberService.deleteProfileImage(memberId);

		return ResponseEntity.ok(null);
	}
}
