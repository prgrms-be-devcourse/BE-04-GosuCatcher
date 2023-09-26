package com.foo.gosucatcher.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	//서버
	INTERNAL_SERVER_ERROR("S001", "예기치 못한 오류가 발생했습니다."),
	NOT_FOUND_REFRESH_TOKEN("S002", "헤더에 RefreshToken이 필요합니다."),
	INVALID_TOKEN("S003", "유효하지 않은 토큰입니다."),

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
	DUPLICATED_EXPERT_STORENAME("E002", "상점명이 중복될 수 없습니다."),
	INVALID_MAX_TRAVEL_DISTANCE("E003", "최대 이동거리는 0 이상이어야 합니다."),
	ALREADY_REGISTERED_BY_SUB_ITEM("E004", "해당 서비스로는 이미 등록되어있습니다."),
	NOT_FOUND_EXPERT_SORT_TYPE("E005", "존재하지 않는 고수 찾기 정렬 타입입니다."),

	//리뷰
	NOT_FOUND_REVIEW("R001", "존재하지 않는 리뷰입니다"),
	UNSUPPORTED_REPLIER("R002", "리뷰에 대한 답장은 서비스를 제공한 고수만 가능합니다."),
	NOT_FOUND_REPLY("R003", "존재하지 않는 리뷰에 대한 답장입니다."),
	UNSUPPORTED_MULTIPLE_REPLIES("R004", "리뷰에 대한 답장은 1개 이하만 작성할 수 있습니다"),
	INVALID_UPDATER("R005", "본인이 작성한 리뷰만 수정할 수 있습니다"),

	//회원
	NOT_FOUND_MEMBER("M001", "존재하지 않는 회원입니다."),
	DUPLICATED_MEMBER("M002", "이미 가입된 회원입니다."),
	LOG_IN_FAILURE("M003", "로그인에 실패했습니다."),
	CERTIFICATION_FAIL("M004", "회원인증에 실패했습니다."),
	ALREADY_CERTIFIED("M005", "이미 인증된 회원입니다."),
	INVALID_EMAIL_FORMAT("M006", "올바르지 않은 이메일 형식입니다."),
	NOT_VALID_REFRESH_TOKEN("M006", "유효하지 않은 RefreshToken 입니다."),
	INCORRECT_AUTH_NUMBER("M007", "잘못된 인증번호입니다."),
	INVALID_AUTH("M008", "유효하지 않은 인증입니다."),
	NOT_CREATION_AUTH_MESSAGE("M009", "인증 메시지를 생성할 수 없습니다."),
	EXPIRED_AUTHENTICATION("M010", "만료된 회원인증입니다."),

	//파일
	NOT_FOUND_IMAGE("F001", "존재하지 않는 이미지 입니다."),
	EMPTY_IMAGE("F002", "이미지를 업로드 바랍니다."),
	INVALID_IMAGE_FORMAT("F003", "지원하지 않는 이미지 파일 형식입니다."),
	EXCESSIVE_IMAGE_COUNT("F004", "업로드 가능한 이미지 최대 개수를 초과하였습니다."),

	//회원 요청 견적서
	NOT_FOUND_MEMBER_ESTIMATE("ME001", "존재하지 않는 회원 요청 견적서입니다."),
	INVALID_MEMBER_ESTIMATE_START_DATE("ME002", "시작 희망 날짜는 현재보다 이전일 수 없습니다."),
	DUPLICATE_MEMBER_ESTIMATE("ME003", "회원 요청 견적서는 중복될 수 없습니다."),
	ALREADY_REQUESTER_HAS_SAME_SUB_ITEM("ME004", "회원과 같은 분야의 서비스는 요청할 수 없습니다."),

	//고수 응답 견적서
	NOT_FOUND_EXPERT_ESTIMATE("EE001", "존재하지 않는 고수가 응답한 견적서 입니다."),
	TOTAL_AMOUNT_CANNOT_BE_LESS_THAN_ZERO("EE002", "총 금액은 0원보다 적을 수 없습니다."),
	ALREADY_REGISTERED_SUB_ITEMS("EE004", "이미 해당 서비스로 바로 견적이 등록되어 있습니다."),
	ALREADY_REQUESTED_ESTIMATE("EE005", "이미 처리된 요청서 입니다."),
	NOT_REGISTERED_SUB_ITEMS("EE006", "등록하지 않은 서비스에 대한 견적서는 만들 수 없습니다."),

	//찜
	NOT_FOUND_BUCKET("B001", "찜 내역이 존재하지 않습니다."),
	UNSUPPORTED_SELF_BUCKET("B002", "자기 자신을 찜할 수 없습니다."),

	//고수&서비스
	NOT_FOUND_EXPERT_ITEM("EI001", "해당 고수는 요청한 서비스를 등록하지 않았습니다."),

	//채팅방
	NOT_FOUND_CHATTING_ROOM("CR001", "채팅방이 존재하지 않습니다."),
	CHATTING_ROOM_ASSIGNMENT_FAILED("CR002", "채팅방 할당에 실패했습니다."),

	//채팅 메시지
	NOT_FOUND_MESSAGE("CM001", "채팅 메시지가 존재하지 않습니다."),

	//JWT
	MALFORMED_JWT("JT001", "유효한 Jws의 형태가 아닙니다."),
	EXPIRED_JWT("JT002", "Jwt의 유효시간이 만료되었습니다."),
	UNSUPPORTED_JWT("JT003", "지원하지 않는 Jws 값입니다."),
	INVALID_SIGNATURE("JT004", "서명이 올바르지 않습니다."),
	EMPTY_OR_NULL_JWT("JT005", "Jwt값이 비어있거나 NULL입니다."),
	INVALID_SECRET_KEY("JT006", "잘못된 Secret Key 값입니다."),
	NOT_EXIST_CLAIM("JT007", "해당  Claim이 존재하지 않습니다."),
	EMPTY_OR_NULL_CLAIM("JT008", "해당 Claim이 비어있거나 NULL입니다."),
	SHORT_OF_JWT_LENGTH("JT009", "Jwt의 길이가 너무 짧거나 Bearer가 없습니다.");

	private final String code;
	private final String message;
}
