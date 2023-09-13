package com.foo.gosucatcher.domain.member.exception;

import com.foo.gosucatcher.global.error.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailAuthException extends RuntimeException {

	private final ErrorCode errorCode;
}
