package com.foo.gosucatcher.domain.bucket.exception;

import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.BusinessException;

public class UnsupportedBucketException extends BusinessException {

	public UnsupportedBucketException(ErrorCode errorCode) {
		super(errorCode);
	}
}
