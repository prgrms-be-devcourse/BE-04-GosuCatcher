package com.foo.gosucatcher.domain.member.application.dto.request;

public record MemberPasswordFoundRequest(
	String email,
	String name
) {
}
