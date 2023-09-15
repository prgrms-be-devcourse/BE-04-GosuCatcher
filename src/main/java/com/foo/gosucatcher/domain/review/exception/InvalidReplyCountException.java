package com.foo.gosucatcher.domain.review.exception;

import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.BusinessException;

public class InvalidReplyCountException extends BusinessException {

	public InvalidReplyCountException(ErrorCode errorCode) {
		super(errorCode);
	}
}
