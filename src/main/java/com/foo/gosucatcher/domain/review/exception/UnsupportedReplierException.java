package com.foo.gosucatcher.domain.review.exception;

import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.BusinessException;

public class UnsupportedReplierException extends BusinessException {

	public UnsupportedReplierException(ErrorCode errorCode) {
		super(errorCode);
	}
}
