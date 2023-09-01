package com.foo.gosucatcher.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	//서버
	INTERNAL_SERVER_ERROR("SERVER_001", "예기치 못한 오류가 발생했습니다.");

	private final String code;
	private final String message;
}
