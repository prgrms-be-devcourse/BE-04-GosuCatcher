package com.foo.gosucatcher.domain.member.presentation;

import java.util.List;

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

import com.foo.gosucatcher.domain.image.application.dto.request.ImageUploadRequest;
import com.foo.gosucatcher.domain.image.application.dto.response.ImageResponse;
import com.foo.gosucatcher.domain.image.application.dto.response.ImagesResponse;
import com.foo.gosucatcher.domain.member.application.MemberProfileService;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberProfileChangeRequest;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberProfileChangeResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberProfileResponse;
import com.foo.gosucatcher.global.aop.CurrentMemberId;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/members/profile")
public class MemberProfileController {

	private final MemberProfileService memberProfileService;

	@CurrentMemberId
	@PatchMapping
	public ResponseEntity<MemberProfileChangeResponse> changeMemberProfile(Long memberId,
		@RequestBody @Validated MemberProfileChangeRequest memberProfileChangeRequest) {
		MemberProfileChangeResponse response = memberProfileService.changeMemberProfile(memberId,
			memberProfileChangeRequest);

		return ResponseEntity.ok(response);
	}

	@CurrentMemberId
	@GetMapping
	public ResponseEntity<MemberProfileResponse> findMemberProfile(Long memberId) {
		MemberProfileResponse response = memberProfileService.findMemberProfile(memberId);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{memberId}")
	public ResponseEntity<MemberProfileResponse> findOtherMemberProfile(@PathVariable Long memberId) {
		MemberProfileResponse response = memberProfileService.findMemberProfile(memberId);

		return ResponseEntity.ok(response);
	}

	@CurrentMemberId
	@PostMapping("/images")
	public ResponseEntity<ImagesResponse> uploadProfileImage(Long memberId, @RequestParam MultipartFile file) {

		ImageUploadRequest request = new ImageUploadRequest(List.of(file));
		ImagesResponse response = memberProfileService.uploadProfileImage(memberId, request);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(response);
	}

	@CurrentMemberId
	@GetMapping("/images")
	public ResponseEntity<ImageResponse> getProfileImage(Long memberId) {
		ImageResponse response = memberProfileService.getProfileImage(memberId);

		return ResponseEntity.ok(response);
	}

	@CurrentMemberId
	@DeleteMapping("/images")
	public ResponseEntity<String> deleteProfileImage(Long memberId) {
		memberProfileService.deleteProfileImage(memberId);

		return ResponseEntity.ok(null);
	}
}
