package com.foo.gosucatcher.domain.member.domain;

import lombok.Getter;

@Getter
public enum Roles {

	ROLE_USER("ROLE_USER"),
	ROLE_EXPERT("ROLE_EXPERT"),
	ROLE_ADMIN("ROLE_ADMIN");

	private final String role;

	Roles(String role) {
		this.role = role;
	}
}
