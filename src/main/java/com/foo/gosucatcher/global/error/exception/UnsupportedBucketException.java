package com.foo.gosucatcher.global.error.exception;

import com.foo.gosucatcher.global.error.ErrorCode;

public class UnsupportedBucketException extends BusinessException {

	public UnsupportedBucketException(ErrorCode errorCode) {
		super(errorCode);
	}
}
