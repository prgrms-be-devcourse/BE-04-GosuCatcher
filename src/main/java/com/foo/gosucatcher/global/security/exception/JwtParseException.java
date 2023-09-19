package com.foo.gosucatcher.global.security.exception;

import com.foo.gosucatcher.global.error.ErrorCode;

public class JwtParseException extends JwtValueException {

	public JwtParseException(ErrorCode errorCode) {
		super(errorCode);
	}
}
