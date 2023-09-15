package com.foo.gosucatcher.global.error.exception;

import com.foo.gosucatcher.global.error.ErrorCode;

public class InvalidReplyCountException extends BusinessException{

	public InvalidReplyCountException(ErrorCode errorCode) {
		super(errorCode);
	}
}
