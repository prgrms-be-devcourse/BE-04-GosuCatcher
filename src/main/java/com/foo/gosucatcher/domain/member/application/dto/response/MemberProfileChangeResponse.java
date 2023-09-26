package com.foo.gosucatcher.domain.member.application.dto.response;

import com.foo.gosucatcher.domain.member.domain.Member;

public record MemberProfileChangeResponse(
	Long id,
	String email
) {

	public static MemberProfileChangeResponse from(Member member) {
		Long memberId = member.getId();
		String memberEmail = member.getEmail();

		return new MemberProfileChangeResponse(memberId, memberEmail);
	}
}
