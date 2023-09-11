package com.foo.gosucatcher.domain.member.application.dto.response;

public record MemberCertifiedResponse(
	String accessToken,
	String refreshToken
) {

	public static MemberCertifiedResponse from(String accessToken, String refreshToken) {
		return new MemberCertifiedResponse(accessToken, refreshToken);
	}
}
