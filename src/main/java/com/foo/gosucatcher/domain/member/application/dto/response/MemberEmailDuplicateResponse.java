package com.foo.gosucatcher.domain.member.application.dto.response;

public record MemberEmailDuplicateResponse(
	String email,
	Boolean isUsable
) {
	public static MemberEmailDuplicateResponse from(String email) {
		return new MemberEmailDuplicateResponse(email, true);
	}
}
