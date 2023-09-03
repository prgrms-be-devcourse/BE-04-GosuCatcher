package com.foo.gosucatcher.domain.member.presentation;

import java.nio.charset.StandardCharsets;

import javax.validation.constraints.Email;

import org.springframework.core.io.Resource;
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
import org.springframework.web.util.UriUtils;

import com.foo.gosucatcher.domain.member.application.MemberService;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberInfoChangeRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberLogInRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberSignUpRequest;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberSignUpResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/members")
public class MemberController {

	private final MemberService memberService;

	public MemberController(MemberService memberService) {
		this.memberService = memberService;
	}

	@PostMapping("/signup")
	public ResponseEntity<MemberSignUpResponse> signUp(
		@RequestBody @Validated MemberSignUpRequest memberSignUpRequest) {
		MemberSignUpResponse memberSignUpResponse = memberService.signUp(memberSignUpRequest);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(memberSignUpResponse);
	}

	@PostMapping("/login")
	public ResponseEntity<Boolean> logIn(
		@RequestBody @Validated MemberLogInRequest memberLogInRequest) {
		memberService.logIn(memberLogInRequest);

		return ResponseEntity.ok(true);
	}

	@GetMapping("/{email}")
	public ResponseEntity<String> findPassword(@PathVariable @Validated @Email String email) {
		String password = memberService.findPassword(email);

		return ResponseEntity.status(HttpStatus.OK)
			.body(password);
	}

	@DeleteMapping("/{memberId}")
	public ResponseEntity<Boolean> deleteMember(@PathVariable long memberId) {
		memberService.deleteMember(memberId);

		return ResponseEntity.ok(true);
	}

	@PatchMapping("/{memberId}")
	public ResponseEntity<Boolean> changeMemberInfo(@PathVariable long memberId,
		@RequestBody @Validated MemberInfoChangeRequest memberInfoChangeRequest) {
		memberService.changeMemberInfo(memberId, memberInfoChangeRequest);

		return ResponseEntity.ok(true);
	}

	@PostMapping("/{memberId}/profile")
	public ResponseEntity<Boolean> uploadProfileImage(@PathVariable long memberId, @RequestParam MultipartFile file) {
		memberService.uploadProfileImage(memberId, file);

		return ResponseEntity.ok(true);
	}

	@GetMapping("/{memberId}/profile")
	public ResponseEntity<Resource> findProfileImage(@PathVariable long memberId) {
		Resource profileImage = memberService.findProfileImage(memberId);

		String originalFileName = "profile.jpeg";
		String encodedOriginalFileName = UriUtils.encode(originalFileName, StandardCharsets.UTF_8);
		String contentDisposition = "attachment; filename=\"" + encodedOriginalFileName + "\"";

		return ResponseEntity.status(HttpStatus.OK)
			.header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
			.body(profileImage);
	}

	@DeleteMapping("/{memberId}/profile")
	public ResponseEntity<Boolean> deleteProfileImage(@PathVariable long memberId) {
		memberService.removeProfileImage(memberId);

		return ResponseEntity.ok(true);
	}
}
