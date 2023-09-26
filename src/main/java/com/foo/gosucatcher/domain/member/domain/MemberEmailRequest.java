package com.foo.gosucatcher.domain.member.domain;

import lombok.Getter;

@Getter
public class MemberEmailRequest {
	@javax.validation.constraints.Email(message = "올바른 이메일 형식을 입력하세요.")
	String email;
}
