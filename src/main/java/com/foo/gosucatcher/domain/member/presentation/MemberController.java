package com.foo.gosucatcher.domain.member.presentation;

import javax.validation.constraints.Email;

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
import org.springframework.web.bind.annotation.RestController;

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
	public ResponseEntity<Boolean> uploadProfileImage(@PathVariable long memberId) {

		return ResponseEntity.ok(true);
	}


}
