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

	//고수
	NOT_FOUND_EXPERT("E001", "존재하지 않는 고수입니다."),

	//리뷰
	NOT_FOUND_REVIEW("R001", "존재하지 않는 리뷰입니다"),

	//회원
	NOT_FOUND_MEMBER("M001", "존재하지 않는 회원입니다."),
	DUPLICATED_MEMBER_EMAIL("M002", "중복된 이메일입니다."),
	NOT_FOUND_MEMBER_EMAIL("M003", "존재하지 않는 이메일입니다."),
	LOG_IN_FAILURE("M004", "로그인에 실패했습니다."),

	//파일
	NOT_FOUND_IMAGE("F001", "존재하지 않는 이미지 입니다."),

	//회원 요청 견적서
	NOT_FOUND_MEMBER_REQUEST_ESTIMATE("MRE001", "존재하지 않는 회원 요청 견적서입니다."),
	INVALID_START_DATE("MRE002", "시작 희망 날짜는 현재보다 이전일 수 없습니다.");

	private final String code;
	private final String message;
}
