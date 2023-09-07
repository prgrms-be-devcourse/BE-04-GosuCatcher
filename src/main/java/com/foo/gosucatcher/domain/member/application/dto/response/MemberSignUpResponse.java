package com.foo.gosucatcher.domain.member.application.dto.response;

import com.foo.gosucatcher.domain.member.domain.Member;

public record MemberSignUpResponse(
	String email,
	String name
) {

	public static MemberSignUpResponse from(Member member) {
		String memberEmail = member.getEmail();
		String memberName = member.getName();

		return new MemberSignUpResponse(memberEmail, memberName);
	}
}
