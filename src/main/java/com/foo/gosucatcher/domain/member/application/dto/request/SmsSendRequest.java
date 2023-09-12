package com.foo.gosucatcher.domain.member.application.dto.request;

import org.intellij.lang.annotations.RegExp;

public record SmsSendRequest(
	@RegExp()
	String phoneNumber
) {
}
