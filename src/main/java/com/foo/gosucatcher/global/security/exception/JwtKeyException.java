package com.foo.gosucatcher.global.security.exception;

import com.foo.gosucatcher.global.error.ErrorCode;

public class JwtKeyException extends JwtValueException {

	public JwtKeyException(ErrorCode errorCode) {
		super(errorCode);
	}
}
