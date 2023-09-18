package com.foo.gosucatcher.domain.image.exception;

import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.BusinessException;

public class InvalidFileTypeException extends BusinessException {

	public InvalidFileTypeException(ErrorCode errorCode) {
		super(errorCode);
	}
}
