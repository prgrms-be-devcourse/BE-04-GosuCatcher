package com.foo.gosucatcher.global.error.exception;

import com.foo.gosucatcher.global.error.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AopException extends RuntimeException {

	private final ErrorCode errorCode;
}
