package com.foo.gosucatcher.global.error.exception;

import com.foo.gosucatcher.global.error.ErrorCode;

public class UnsupportedReplierException extends BusinessException {

	public UnsupportedReplierException(ErrorCode errorCode) {
		super(errorCode);
	}
}
