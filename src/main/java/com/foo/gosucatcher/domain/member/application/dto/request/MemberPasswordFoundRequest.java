package com.foo.gosucatcher.domain.member.application.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

public record MemberPasswordFoundRequest(
	@Email(message = "올바른 이메일 형식을 입력하세요")
	String email,
	@NotBlank(message = "이름은 비어있을 수 없습니다")
	@Length(min = 2, max = 20, message = "이름은 2자 이상 20자 이하로 입력 가능합니다")
	String name
) {
}
