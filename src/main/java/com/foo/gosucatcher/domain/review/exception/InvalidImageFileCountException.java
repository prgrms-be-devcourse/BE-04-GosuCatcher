package com.foo.gosucatcher.domain.review.exception;

import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.BusinessException;

public class InvalidImageFileCountException extends BusinessException {

	public InvalidImageFileCountException(ErrorCode errorCode) {
		super(errorCode);
	}
}
