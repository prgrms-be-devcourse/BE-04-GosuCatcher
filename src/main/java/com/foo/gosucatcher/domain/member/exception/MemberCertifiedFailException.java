package com.foo.gosucatcher.domain.member.exception;

import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.InvalidValueException;

public class MemberCertifiedFailException extends InvalidValueException {
	public MemberCertifiedFailException(ErrorCode errorCode) {
		super(errorCode);
	}
}
