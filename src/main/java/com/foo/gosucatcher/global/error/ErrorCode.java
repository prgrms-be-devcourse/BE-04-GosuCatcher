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

	//메인 서비스
	NOT_FOUND_MAIN_ITEM("MI001", "메인 서비스를 찾을 수 없습니다."),
	DUPLICATED_MAIN_ITEM_NAME("MI002", "메인 서비스 이름이 중복될 수 없습니다."),

	//하위 서비스
	NOT_FOUND_SUB_ITEM("SI001", "존재하지 않는 하위 서비스입니다"),
	DUPLICATED_SUB_ITEM_NAME("SI002", "하위 서비스 이름이 중복될 수 없습니다."),

	;

	private final String code;
	private final String message;
}
