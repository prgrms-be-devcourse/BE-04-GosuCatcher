package com.foo.gosucatcher.domain.member.application.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

public record MemberLogInRequest(
	@Email String email,
	@NotEmpty @NotBlank String password
) {
}
