package com.foo.gosucatcher.domain.member.presentation;

import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
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
import com.foo.gosucatcher.domain.member.application.dto.request.MemberEmailAuthRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberLoginRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberPasswordFoundRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberProfileChangeRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberSignupRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.ProfileImageUploadRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.SmsAuthRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.SmsSendRequest;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberCertifiedResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberEmailAuthResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberEmailSendResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberPasswordFoundResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberProfileChangeResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberProfileResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberSignupResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.ProfileImageUploadResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.SmsAuthResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.SmsSendResponse;
import com.foo.gosucatcher.domain.member.domain.ImageFile;
import com.foo.gosucatcher.global.aop.CurrentMemberEmail;
import com.foo.gosucatcher.global.aop.CurrentMemberId;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.InvalidValueException;
import com.foo.gosucatcher.global.util.ImageFileUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/members")
public class MemberController {

	private final MemberService memberService;

	@GetMapping("/signup/auth")
	public ResponseEntity<MemberEmailSendResponse> sendAuthEmail(@RequestParam String email) {
		isValidEmail(email);

		memberService.checkDuplicatedEmail(email);
		MemberEmailSendResponse authenticateResponse = memberService.sendAuthEmail(email);

		return ResponseEntity.ok(authenticateResponse);
	}

	@PostMapping("/signup/auth/email")
	public ResponseEntity<MemberEmailAuthResponse> authenticateMemberByEmail(
		@RequestBody @Validated MemberEmailAuthRequest memberEmailAuthRequest) {
		MemberEmailAuthResponse memberEmailAuthResponse = memberService.authenticateMemberByEmail(
			memberEmailAuthRequest);

		return ResponseEntity.ok(memberEmailAuthResponse);
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

	@CurrentMemberEmail
	@DeleteMapping("/logout")
	public ResponseEntity<Void> logout(String memberEmail) {
		isValidEmail(memberEmail);

		memberService.logout(memberEmail);

		return ResponseEntity.noContent().build();
	}

	@PostMapping("/recovery/password")
	public ResponseEntity<MemberPasswordFoundResponse> findPassword(
		@RequestBody @Validated MemberPasswordFoundRequest memberPasswordFoundRequest) {
		MemberPasswordFoundResponse response = memberService.findPassword(memberPasswordFoundRequest);

		return ResponseEntity.ok(response);
	}

	@CurrentMemberId
	@DeleteMapping
	public ResponseEntity<Void> deleteMember(Long memberId) {
		memberService.deleteMember(memberId);

		return ResponseEntity.noContent().build();
	}

	@CurrentMemberId
	@GetMapping("/profiles")
	public ResponseEntity<MemberProfileResponse> findMemberProfile(Long memberId) {
		MemberProfileResponse response = memberService.findMemberProfile(memberId);

		return ResponseEntity.ok(response);
	}

	@CurrentMemberId
	@PostMapping("/profiles/auth/phone")
	public ResponseEntity<SmsSendResponse> sendAuthSms(Long memberId,
		@RequestBody @Validated SmsSendRequest smsSendRequest) {
		SmsSendResponse smsAuthResponse = memberService.sendSms(memberId, smsSendRequest);

		return ResponseEntity.ok(smsAuthResponse);
	}

	@CurrentMemberId
	@PostMapping("/profiles/auth")
	public ResponseEntity<SmsAuthResponse> authenticateSms(Long memberId,
		@RequestBody @Validated SmsAuthRequest smsAuthRequest) {
		SmsAuthResponse smsAuthResponse = memberService.authenticateSms(memberId, smsAuthRequest);

		return ResponseEntity.ok(smsAuthResponse);
	}

	@CurrentMemberId
	@PatchMapping("/profiles")
	public ResponseEntity<MemberProfileChangeResponse> changeMemberProfile(Long memberId,
		@RequestBody @Validated MemberProfileChangeRequest memberProfileChangeRequest) {
		MemberProfileChangeResponse response = memberService.changeMemberProfile(memberId,
			memberProfileChangeRequest);

		return ResponseEntity.ok(response);
	}

	//todo: 리팩토링 예정
	@CurrentMemberId
	@PostMapping("/profiles/image")
	public ResponseEntity<ProfileImageUploadResponse> uploadProfileImage(Long memberId,
		@RequestParam MultipartFile file) {
		ProfileImageUploadRequest request = new ProfileImageUploadRequest(memberId, file);
		memberService.uploadProfileImage(request);

		ProfileImageUploadResponse response = new ProfileImageUploadResponse(memberId, file.getOriginalFilename());

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(response);
	}

	@GetMapping("/{id}/profiles/image")
	public ResponseEntity<Resource> findProfileImage(@PathVariable Long id) {
		ImageFile profileImage = memberService.findProfileImage(id);

		String contentDisposition = ImageFileUtils.makeImageFileContentDisposition(profileImage);
		UrlResource resource = ImageFileUtils.makeImageFileUrlResource(profileImage);

		return ResponseEntity.status(HttpStatus.OK)
			.header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
			.body(resource);
	}

	@CurrentMemberId
	@DeleteMapping("/profiles/image")
	public ResponseEntity<Void> deleteProfileImage(Long memberId) {
		memberService.deleteProfileImage(memberId);

		return ResponseEntity.noContent().build();
	}

	private void isValidEmail(String email) {
		EmailValidator emailValidator = new EmailValidator();
		if (!emailValidator.isValid(email, null)) {
			throw new InvalidValueException(ErrorCode.INVALID_EMAIL_FORMAT);
		}
	}
}
