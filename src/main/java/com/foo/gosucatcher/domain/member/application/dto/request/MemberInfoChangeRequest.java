package com.foo.gosucatcher.domain.member.application.dto.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

public record MemberInfoChangeRequest(
	@NotEmpty @Min(2) @Max(20) String name,
	@Min(5) @Max(20) String password,
	String phoneNumber
) {
}
