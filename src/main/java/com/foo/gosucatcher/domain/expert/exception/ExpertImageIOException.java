package com.foo.gosucatcher.domain.expert.exception;

import com.foo.gosucatcher.global.error.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExpertImageIOException extends RuntimeException {

	private final ErrorCode errorCode;
}
