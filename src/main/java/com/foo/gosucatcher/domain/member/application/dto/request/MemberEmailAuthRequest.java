package com.foo.gosucatcher.domain.member.application.dto.request;

import org.hibernate.validator.constraints.Length;

public record MemberEmailAuthRequest(

	@Length(min = 6, max = 6, message = "6자리 숫자를 입력하세요.")
	String authNumber
) {
}
