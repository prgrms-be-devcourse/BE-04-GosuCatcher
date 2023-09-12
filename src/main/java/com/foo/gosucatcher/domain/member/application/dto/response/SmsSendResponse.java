package com.foo.gosucatcher.domain.member.application.dto.response;

import com.foo.gosucatcher.domain.member.domain.Member;

public record SmsSendResponse(
	String memberName,
	String phoneNumber
) {
	public static SmsSendResponse from(Member member, String toNumber) {
		String name = member.getName();

		return new SmsSendResponse(name, toNumber);
	}
}
