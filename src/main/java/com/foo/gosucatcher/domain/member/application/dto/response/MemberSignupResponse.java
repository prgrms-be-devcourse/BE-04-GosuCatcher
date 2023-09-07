package com.foo.gosucatcher.domain.member.application.dto.response;

import com.foo.gosucatcher.domain.member.domain.Member;

public record MemberSignupResponse(
	String email,
	String name
) {

	public static MemberSignupResponse from(Member member) {
		String memberEmail = member.getEmail();
		String memberName = member.getName();

		return new MemberSignupResponse(memberEmail, memberName);
	}
}
