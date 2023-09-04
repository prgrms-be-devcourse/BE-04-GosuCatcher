package com.foo.gosucatcher.domain.member.application.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import com.foo.gosucatcher.domain.member.domain.Member;

public record MemberSignUpRequest(
	@NotEmpty @NotBlank @Min(2) @Max(20) String name,
	@Email @Max(50) String email,
	@NotEmpty @NotBlank @Min(5) @Max(20) String password
) {
	public static Member to(MemberSignUpRequest memberSignUpRequest) {
		return Member.builder()
			.name(memberSignUpRequest.name)
			.email(memberSignUpRequest.email)
			.password(memberSignUpRequest.password)
			.build();
	}
}
