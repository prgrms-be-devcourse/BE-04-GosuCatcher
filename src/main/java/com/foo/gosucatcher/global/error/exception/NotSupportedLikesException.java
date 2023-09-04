package com.foo.gosucatcher.global.error.exception;

import com.foo.gosucatcher.global.error.ErrorCode;

public class NotSupportedLikesException extends BusinessException {

	public NotSupportedLikesException(ErrorCode errorCode) {
		super(errorCode);
	}
}
