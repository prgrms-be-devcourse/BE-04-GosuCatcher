package com.foo.gosucatcher.global.security.exception;

import com.foo.gosucatcher.global.error.ErrorCode;

public class JwtHeaderException extends JwtValueException {

	public JwtHeaderException(ErrorCode errorCode) {
		super(errorCode);
	}
}
