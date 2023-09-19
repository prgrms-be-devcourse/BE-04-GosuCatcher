package com.foo.gosucatcher.global.security.exception;

import com.foo.gosucatcher.global.error.ErrorCode;

import io.jsonwebtoken.JwtException;
import lombok.Getter;

@Getter
public abstract class JwtValueException extends JwtException {

	private final ErrorCode errorCode;

	public JwtValueException(ErrorCode errorCode) {
		super("Jwt 값이 올바르지 않습니다.");
		this.errorCode = errorCode;
	}
}
