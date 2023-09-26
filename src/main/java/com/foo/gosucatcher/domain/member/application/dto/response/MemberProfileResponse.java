package com.foo.gosucatcher.domain.member.application.dto.response;

import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.domain.member.domain.Roles;

public record MemberProfileResponse(
	String email,
	String name,
	String phoneNumber,
	Roles role
) {

	public static MemberProfileResponse from(Member member) {
		String memberEmail = member.getEmail();
		String memberName = member.getName();
		String memberPhoneNumber = member.getPhoneNumber();
		Roles memberRole = member.getRole();

		return new MemberProfileResponse(memberEmail, memberName, memberPhoneNumber, memberRole);
	}
}
