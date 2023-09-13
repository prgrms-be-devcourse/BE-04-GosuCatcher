package com.foo.gosucatcher.domain.member.application.dto.response;

public record SmsAuthResponse(
	String phoneNumber,
	Boolean isSuccess
) {
}
