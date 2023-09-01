package com.foo.gosucatcher.domain.member.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foo.gosucatcher.domain.member.application.MemberService;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberSignUpRequest;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberSignUpResponse;

@RestController
@RequestMapping("/v1/members")
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
}
