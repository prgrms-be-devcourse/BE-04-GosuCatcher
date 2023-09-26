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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "MemberProfileController",
	description = "프로필 정보 조회/수정/역할변경, 프로필 이미지 업로드/조회/삭제 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/members/profile")
public class MemberProfileController {

	private final MemberProfileService memberProfileService;

	@CurrentMemberId
	@PatchMapping
	@Operation(summary = "회원 프로필 정보 변경", description = "회원의 이름, 비밀번호, 핸드폰번호를 변경합니다.")
	public ResponseEntity<MemberProfileChangeResponse> changeMemberProfile(
		@Parameter(description = "토큰에서 가져온 멤버ID", required = true)
		Long memberId,
		@RequestBody @Validated
		@Parameter(description = "정보 변경에 필요한 요청 정보", required = true)
		MemberProfileChangeRequest memberProfileChangeRequest
	) {
		MemberProfileChangeResponse response = memberProfileService.changeMemberProfile(memberId,
			memberProfileChangeRequest);

		return ResponseEntity.ok(response);
	}

	@CurrentMemberId
	@GetMapping
	@Operation(summary = "본인의 회원 정보 조회", description = "회원 아이디, 회원의 이름, 비밀번호, 핸드폰번호를 조회합니다.")
	public ResponseEntity<MemberProfileResponse> findMemberProfile(
		@Parameter(description = "토큰에서 가져온 멤버ID", required = true)
		Long memberId
	) {
		MemberProfileResponse response = memberProfileService.findMemberProfile(memberId);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{memberId}")
	@Operation(summary = "다른 회원의 정보 조회", description = "다른 회원의 아이디, 회원의 이름, 비밀번호, 핸드폰번호를 조회합니다.")
	public ResponseEntity<MemberProfileResponse> findOtherMemberProfile(
		@PathVariable
		@Parameter(description = "조회하고자 하는 멤버의 ID", required = true)
		Long memberId
	) {
		MemberProfileResponse response = memberProfileService.findMemberProfile(memberId);

		return ResponseEntity.ok(response);
	}

	@CurrentMemberId
	@PatchMapping("/role")
	@Operation(summary = "회원의 역할을 변경합니다.", description = "고수는 유저로, 유저는 고수로 역할을 변경합니다.")
	public ResponseEntity<Long> changeMemberRole(
		@Parameter(description = "토큰에서 가져온 멤버ID", required = true)
		Long memberId
	) {
		Long updatedMemberId = memberProfileService.changeMemberRole(memberId);

		return ResponseEntity.ok(updatedMemberId);
	}

	@CurrentMemberId
	@PostMapping("/images")
	@Operation(summary = "회원 프로필 이미지 업로드", description = "회원의 프로필 이미지를 업로드합니다.")
	public ResponseEntity<ImagesResponse> uploadProfileImage(
		@Parameter(description = "토큰에서 가져온 멤버ID", required = true)
		Long memberId,
		@RequestParam
		@Parameter(description = "이미지 파일 객체", required = true)
		MultipartFile file
	) {

		ImageUploadRequest request = new ImageUploadRequest(List.of(file));
		ImagesResponse response = memberProfileService.uploadProfileImage(memberId, request);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(response);
	}

	@CurrentMemberId
	@GetMapping("/images")
	@Operation(summary = "회원 프로필 이미지 조회", description = "회원의 프로필 이미지를 조회합니다.")
	public ResponseEntity<ImageResponse> getProfileImage(
		@Parameter(description = "토큰에서 가져온 멤버ID", required = true)
		Long memberId
	) {
		ImageResponse response = memberProfileService.getProfileImage(memberId);

		return ResponseEntity.ok(response);
	}

	@CurrentMemberId
	@DeleteMapping("/images")
	@Operation(summary = "회원 프로필 이미지 삭제", description = "회원의 프로필 이미지를 삭제합니다.")
	public ResponseEntity<String> deleteProfileImage(
		@Parameter(description = "토큰에서 가져온 멤버ID", required = true)
		Long memberId
	) {
		memberProfileService.deleteProfileImage(memberId);

		return ResponseEntity.ok(null);
	}
}
