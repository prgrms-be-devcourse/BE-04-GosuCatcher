package com.foo.gosucatcher.domain.member.application.dto.response;

import com.foo.gosucatcher.domain.member.domain.Member;

public record MemberLogInResponse(
	Long id,
	String email
) {

	public static MemberLogInResponse from(Member logInMember) {
		Long id = logInMember.getId();
		String email = logInMember.getEmail();

		return new MemberLogInResponse(id, email);
	}
}
