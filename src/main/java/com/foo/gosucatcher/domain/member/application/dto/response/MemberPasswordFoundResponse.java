package com.foo.gosucatcher.domain.member.application.dto.response;

import com.foo.gosucatcher.domain.member.domain.Member;

public record MemberPasswordFoundResponse(
	String name,
	String password
) {
	public static MemberPasswordFoundResponse to(Member member) {
		String memberEmail = member.getEmail();
		String memberPassword = member.getPassword();

		return new MemberPasswordFoundResponse(memberEmail, memberPassword);
	}
}
