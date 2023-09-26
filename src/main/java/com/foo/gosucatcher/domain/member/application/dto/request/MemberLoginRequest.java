package com.foo.gosucatcher.domain.member.application.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import com.foo.gosucatcher.domain.member.domain.Member;

public record MemberLoginRequest(
	@Email(message = "올바른 이메일 형식을 입력하세요")
	@Length(max = 50)
	String email,
	@NotBlank(message = "비밀번호는 비어있을 수 없습니다")
	@Length(min = 5, max = 20, message = "비밀번호는 5자 이상 20자 이하로 입력 가능합니다")
	String password
) {
	public static Member toMember(MemberLoginRequest request) {
		String loginRequestEmail = request.email();
		String loginRequestPassword = request.password();

		return Member.builder()
			.email(loginRequestEmail)
			.password(loginRequestPassword)
			.build();
	}
}
