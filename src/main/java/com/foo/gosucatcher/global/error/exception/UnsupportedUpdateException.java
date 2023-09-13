package com.foo.gosucatcher.global.error.exception;

import com.foo.gosucatcher.global.error.ErrorCode;

public class UnsupportedUpdateException extends BusinessException {

	public UnsupportedUpdateException(ErrorCode errorCode) {
		super(errorCode);
	}
}
