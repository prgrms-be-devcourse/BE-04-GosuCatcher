package com.foo.gosucatcher.domain.member.application.dto.response;

import com.foo.gosucatcher.domain.member.domain.Member;

public record MemberPasswordFoundResponse(
	String email,
	Boolean isSuccess
) {

	public static MemberPasswordFoundResponse from(Member member) {
		String memberEmail = member.getEmail();

		return new MemberPasswordFoundResponse(memberEmail, true);
	}
}
