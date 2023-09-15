package com.foo.gosucatcher.global.error.exception;

import com.foo.gosucatcher.global.error.ErrorCode;

public class InvalidFileSizeException extends BusinessException{

	public InvalidFileSizeException(ErrorCode errorCode) {
		super(errorCode);
	}
}
