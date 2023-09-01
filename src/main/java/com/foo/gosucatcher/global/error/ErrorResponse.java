package com.foo.gosucatcher.global.error;

import java.time.LocalDateTime;

public record ErrorResponse(
	LocalDateTime timestamp,
	String code,
	String message
) {

	public static ErrorResponse from(ErrorCode errorCode) {
		return new ErrorResponse(LocalDateTime.now(), errorCode.getCode(), errorCode.getMessage());
	}
}
