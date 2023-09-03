package com.foo.gosucatcher.domain.member.application.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import com.foo.gosucatcher.domain.member.domain.Member;

public record MemberSignUpRequest(
	@NotEmpty @NotBlank String name,
	@Email String email,
	@NotEmpty @NotBlank String password
) {
	public static Member to(MemberSignUpRequest memberSignUpRequest) {
		return Member.builder()
			.name(memberSignUpRequest.name)
			.email(memberSignUpRequest.email)
			.password(memberSignUpRequest.password)
			.build();
	}
}
