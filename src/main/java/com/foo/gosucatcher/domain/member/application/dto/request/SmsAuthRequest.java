package com.foo.gosucatcher.domain.member.application.dto.request;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

public record SmsAuthRequest(
	@Pattern(regexp = "^01[016-9][1-9]\\d{6,7}$", message = "휴대폰 번호를 - 없이 11자리 입력해주세요.")
	String phoneNumber,
	@Length(min = 6, max = 6, message = "6자리 숫자를 입력하세요.")
	String authNumber
) {
}
