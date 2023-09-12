package com.foo.gosucatcher.domain.member.presentation;

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

import com.foo.gosucatcher.domain.member.application.MemberProfileService;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberProfileChangeRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.ProfileImageUploadRequest;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberProfileChangeResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberProfileResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.ProfileImageUploadResponse;
import com.foo.gosucatcher.domain.member.domain.ImageFile;
import com.foo.gosucatcher.global.aop.CurrentMemberId;
import com.foo.gosucatcher.global.util.ImageFileUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/members")
public class MemberProfileController {

	private final MemberProfileService memberProfileService;

	@CurrentMemberId
	@DeleteMapping
	public ResponseEntity<Void> deleteMember(Long memberId) {
		memberProfileService.deleteMember(memberId);

		return ResponseEntity.noContent().build();
	}

	@CurrentMemberId
	@GetMapping("/profiles")
	public ResponseEntity<MemberProfileResponse> findMemberProfile(Long memberId) {
		MemberProfileResponse response = memberProfileService.findMemberProfile(memberId);

		return ResponseEntity.ok(response);
	}

	@CurrentMemberId
	@PatchMapping("/profiles")
	public ResponseEntity<MemberProfileChangeResponse> changeMemberProfile(Long memberId,
		@RequestBody @Validated MemberProfileChangeRequest memberProfileChangeRequest) {
		MemberProfileChangeResponse response = memberProfileService.changeMemberProfile(memberId,
			memberProfileChangeRequest);

		return ResponseEntity.ok(response);
	}

	//todo: 리팩토링 예정
	@CurrentMemberId
	@PostMapping("/profiles/image")
	public ResponseEntity<ProfileImageUploadResponse> uploadProfileImage(Long memberId,
		@RequestParam MultipartFile file) {
		ProfileImageUploadRequest request = new ProfileImageUploadRequest(memberId, file);
		memberProfileService.uploadProfileImage(request);

		ProfileImageUploadResponse response = new ProfileImageUploadResponse(memberId, file.getOriginalFilename());

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(response);
	}

	@GetMapping("/{id}/profiles/image")
	public ResponseEntity<Resource> findProfileImage(@PathVariable Long id) {
		ImageFile profileImage = memberProfileService.findProfileImage(id);

		String contentDisposition = ImageFileUtils.makeImageFileContentDisposition(profileImage);
		UrlResource resource = ImageFileUtils.makeImageFileUrlResource(profileImage);

		return ResponseEntity.status(HttpStatus.OK)
			.header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
			.body(resource);
	}

	@CurrentMemberId
	@DeleteMapping("/profiles/image")
	public ResponseEntity<Void> deleteProfileImage(Long memberId) {
		memberProfileService.deleteProfileImage(memberId);

		return ResponseEntity.noContent().build();
	}
}
