package com.foo.gosucatcher.domain.member.presentation;

import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;

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
import org.springframework.web.util.UriUtils;

import com.foo.gosucatcher.domain.member.application.MemberService;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberInfoChangeRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberLogInRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberSignUpRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.ProfileImageUploadRequest;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberPasswordFoundResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.ProfileImageUploadResponse;
import com.foo.gosucatcher.domain.member.domain.ImageFile;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.InvalidValueException;

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
	public ResponseEntity<Void> signUp(
		@RequestBody @Validated MemberSignUpRequest memberSignUpRequest) {
		memberService.signUp(memberSignUpRequest);

		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PostMapping("/login")
	public ResponseEntity<Void> logIn(
		@RequestBody @Validated MemberLogInRequest memberLogInRequest) {
		memberService.logIn(memberLogInRequest);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/signup")
	public ResponseEntity<Void> checkDuplicatedEmail(@RequestParam @Validated @Email String email) {
		memberService.checkDuplicatedEmail(email);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/{email}")
	public ResponseEntity<MemberPasswordFoundResponse> findPassword(@PathVariable @Validated @Email String email) {
		var response = memberService.findPassword(email);

		return ResponseEntity.status(HttpStatus.OK)
			.body(response);
	}

	@PatchMapping("/{memberId}")
	public ResponseEntity<Void> changeMemberInfo(@PathVariable long memberId,
		@RequestBody @Validated MemberInfoChangeRequest memberInfoChangeRequest) {
		memberService.changeMemberInfo(memberId, memberInfoChangeRequest);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/{memberId}")
	public ResponseEntity<Void> deleteMember(@PathVariable long memberId) {
		memberService.deleteMember(memberId);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/{memberId}/profile")
	public ResponseEntity<ProfileImageUploadResponse> uploadProfileImage(@PathVariable long memberId,
		@RequestParam MultipartFile file) {
		var request = new ProfileImageUploadRequest(memberId, file);
		memberService.uploadProfileImage(request);

		var response = new ProfileImageUploadResponse(memberId, file.getOriginalFilename());

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(response);
	}

	@GetMapping("/{memberId}/profile")
	public ResponseEntity<Resource> findProfileImage(@PathVariable long memberId) {
		ImageFile profileImage = memberService.findProfileImage(memberId);

		String contentDisposition = makeContentDisposition(profileImage);
		String path = profileImage.getPath();
		try {
			UrlResource resource = new UrlResource("file:" + path);

			return ResponseEntity.status(HttpStatus.OK)
				.header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
				.body(resource);
		} catch (MalformedURLException e) {
			throw new InvalidValueException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/{memberId}/profile")
	public ResponseEntity<Void> deleteProfileImage(@PathVariable long memberId) {
		memberService.removeProfileImage(memberId);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	private String makeContentDisposition(ImageFile profileImage) {
		String fileName = profileImage.getFileName() + "." + profileImage.getFileExtension();
		String encodedOriginalFileName = UriUtils.encode(fileName, StandardCharsets.UTF_8);

		return "attachment; filename=\"" + encodedOriginalFileName + "\"";
	}
}
