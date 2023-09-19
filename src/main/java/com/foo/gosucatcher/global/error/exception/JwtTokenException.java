package com.foo.gosucatcher.global.error.exception;

import com.foo.gosucatcher.global.error.ErrorCode;

import io.jsonwebtoken.JwtException;
import lombok.Getter;

@Getter
public class JwtTokenException extends JwtException {

	private final ErrorCode errorCode;

	public JwtTokenException(ErrorCode errorCode) {
		super(null);
		this.errorCode = errorCode;
	}
}
