package com.foo.gosucatcher.global.error.exception;

import com.foo.gosucatcher.global.error.ErrorCode;

public class NotSupportedBucketException extends BusinessException {

	public NotSupportedBucketException(ErrorCode errorCode) {
		super(errorCode);
	}
}
