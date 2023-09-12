package com.foo.gosucatcher.domain.member.application.dto.request;

public record SmsAuthRequest(
	String phoneNumber,
	String authNumber
) {
}
