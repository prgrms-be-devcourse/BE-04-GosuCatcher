package com.foo.gosucatcher.global.security;

import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.member.domain.Member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails {
	private final Member member;
	private final Expert expert;
}
