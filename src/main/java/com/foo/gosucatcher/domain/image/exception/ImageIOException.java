package com.foo.gosucatcher.domain.image.exception;

import com.foo.gosucatcher.global.error.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ImageIOException extends RuntimeException {

	private final ErrorCode errorCode;
}
