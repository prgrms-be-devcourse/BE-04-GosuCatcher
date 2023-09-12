package com.foo.gosucatcher.domain.member.application.dto.request;

import javax.validation.constraints.Pattern;

public record SmsSendRequest(
	@Pattern(regexp = "^01[016-9][1-9]\\d{6,7}$", message = "휴대폰 번호를 - 없이 11자리 입력해주세요.")
	String phoneNumber
) {
}
