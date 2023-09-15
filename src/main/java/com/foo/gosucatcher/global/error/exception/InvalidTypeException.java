package com.foo.gosucatcher.global.error.exception;

import com.foo.gosucatcher.global.error.ErrorCode;

public class InvalidTypeException extends BusinessException{

	public InvalidTypeException(ErrorCode errorCode) {
		super(errorCode);
	}
}
