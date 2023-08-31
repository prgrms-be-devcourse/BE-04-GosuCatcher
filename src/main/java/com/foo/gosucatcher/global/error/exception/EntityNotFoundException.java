package com.foo.gosucatcher.global.error.exception;

import com.foo.gosucatcher.global.error.ErrorCode;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends BusinessException {

	public EntityNotFoundException(ErrorCode errorCode) {
		super(errorCode);
	}
}
