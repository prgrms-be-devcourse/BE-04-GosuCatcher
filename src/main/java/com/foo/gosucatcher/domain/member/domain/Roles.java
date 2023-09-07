package com.foo.gosucatcher.domain.member.domain;

import lombok.Getter;

@Getter
public enum Roles {
	USER("USER"),
	ADMIN("ADMIN");

	private final String role;

	Roles(String role) {
		this.role = role;
	}
}
