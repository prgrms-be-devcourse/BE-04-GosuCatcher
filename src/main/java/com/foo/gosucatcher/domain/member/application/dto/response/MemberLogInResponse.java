package com.foo.gosucatcher.domain.member.application.dto.response;

public record MemberLogInResponse(
	String accessToken,
	String refreshToken
) {

	public static MemberLogInResponse from(String accessToken, String refreshToken) {
		return new MemberLogInResponse(accessToken, refreshToken);
	}
}
