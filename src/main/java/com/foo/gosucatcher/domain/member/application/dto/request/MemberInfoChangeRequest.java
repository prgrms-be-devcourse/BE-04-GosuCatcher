package com.foo.gosucatcher.domain.member.application.dto.request;

public record MemberInfoChangeRequest(
	String name,
	String password,
	String phoneNumber
) {
}
