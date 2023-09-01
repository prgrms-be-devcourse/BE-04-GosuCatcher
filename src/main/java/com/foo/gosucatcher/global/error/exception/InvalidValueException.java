package com.foo.gosucatcher.global.error.exception;

import com.foo.gosucatcher.global.error.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class InvalidValueException extends BusinessException {

	public InvalidValueException(ErrorCode errorCode) {
		super(errorCode);
	}
}
