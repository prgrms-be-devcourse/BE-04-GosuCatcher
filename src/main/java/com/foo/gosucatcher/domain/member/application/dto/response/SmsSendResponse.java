package com.foo.gosucatcher.domain.member.application.dto.response;

public record SmsSendResponse(
	Long memberId,
	String phoneNumber
) {

	public static SmsSendResponse from(Long memberId, String toNumber) {
		return new SmsSendResponse(memberId, toNumber);
	}
}
