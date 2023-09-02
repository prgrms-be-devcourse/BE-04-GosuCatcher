package com.foo.gosucatcher.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	//서버
	INTERNAL_SERVER_ERROR("S001", "예기치 못한 오류가 발생했습니다."),

	//공용
	INVALID_INPUT_VALUE("C001", "잘못된 값을 입력하셨습니다."),

	//고수
	NOT_FOUND_EXPERT("E001", "존재하지 않는 고수입니다.");

	private final String code;
	private final String message;
}
