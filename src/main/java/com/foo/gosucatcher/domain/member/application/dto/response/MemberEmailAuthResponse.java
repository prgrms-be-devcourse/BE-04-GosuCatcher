package com.foo.gosucatcher.domain.member.application.dto.response;

public record MemberEmailAuthResponse(
	String email,
	Boolean isAuthSuccess
) {
}
