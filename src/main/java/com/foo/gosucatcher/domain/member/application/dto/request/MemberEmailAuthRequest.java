package com.foo.gosucatcher.domain.member.application.dto.request;

import javax.validation.constraints.Email;

import org.hibernate.validator.constraints.Length;

public record MemberEmailAuthRequest(
	@Email(message = "올바른 이메일 형식을 입력하세요")
	String email,
	@Length(min = 6, max = 6)
	String authNumber
) {
}
