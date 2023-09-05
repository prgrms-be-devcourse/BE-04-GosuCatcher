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
import com.foo.gosucatcher.domain.member.application.dto.request.MemberInfoChangeRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberLogInRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberSignUpRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.ProfileImageUploadRequest;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberLogInResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberPasswordFoundResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.ProfileImageUploadResponse;
import com.foo.gosucatcher.domain.member.domain.ImageFile;
import com.foo.gosucatcher.global.util.ImageFileUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/members")
public class MemberController {

	private final MemberService memberService;

	@PostMapping("/signup")
	public ResponseEntity<Void> signUp(
		@RequestBody @Validated MemberSignUpRequest memberSignUpRequest) {
		memberService.signUp(memberSignUpRequest);

		return ResponseEntity.ok(null);
	}

	@PostMapping("/login")
	public ResponseEntity<MemberLogInResponse> logIn(
		@RequestBody @Validated MemberLogInRequest memberLogInRequest) {
		MemberLogInResponse response = memberService.logIn(memberLogInRequest);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/signup")
	public ResponseEntity<Void> checkDuplicatedEmail(@RequestParam @Validated @Email String email) {
		memberService.checkDuplicatedEmail(email);

		return ResponseEntity.ok(null);
	}

	@GetMapping("/{email}")
	public ResponseEntity<MemberPasswordFoundResponse> findPassword(@PathVariable @Validated @Email String email) {
		MemberPasswordFoundResponse response = memberService.findPassword(email);

		return ResponseEntity.ok(response);
	}

	@PatchMapping("/{memberId}")
	public ResponseEntity<Long> changeMemberInfo(@PathVariable long memberId,
		@RequestBody @Validated MemberInfoChangeRequest memberInfoChangeRequest) {
		long responseId = memberService.changeMemberInfo(memberId, memberInfoChangeRequest);

		return ResponseEntity.ok(responseId);
	}

	@DeleteMapping("/{memberId}")
	public ResponseEntity<Void> deleteMember(@PathVariable long memberId) {
		memberService.deleteMember(memberId);

		return ResponseEntity.ok(null);
	}

	@PostMapping("/{memberId}/profile")
	public ResponseEntity<ProfileImageUploadResponse> uploadProfileImage(@PathVariable long memberId,
		@RequestParam MultipartFile file) {
		ProfileImageUploadRequest request = new ProfileImageUploadRequest(memberId, file);
		memberService.uploadProfileImage(request);

		ProfileImageUploadResponse response = new ProfileImageUploadResponse(memberId, file.getOriginalFilename());

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(response);
	}

	@GetMapping("/{memberId}/profile")
	public ResponseEntity<Resource> findProfileImage(@PathVariable long memberId) {
		ImageFile profileImage = memberService.findProfileImage(memberId);

		String contentDisposition = ImageFileUtils.makeImageFileContentDisposition(profileImage);
		UrlResource resource = ImageFileUtils.makeImageFileUrlResource(profileImage);

		return ResponseEntity.status(HttpStatus.OK)
			.header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
			.body(resource);
	}

	@DeleteMapping("/{memberId}/profile")
	public ResponseEntity<Void> deleteProfileImage(@PathVariable long memberId) {
		memberService.removeProfileImage(memberId);

		return ResponseEntity.ok(null);
	}
}
