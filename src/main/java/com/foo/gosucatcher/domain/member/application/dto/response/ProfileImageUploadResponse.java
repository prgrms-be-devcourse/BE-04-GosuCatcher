package com.foo.gosucatcher.domain.member.application.dto.response;

public record ProfileImageUploadResponse(
	long memberId,
	String originalFilename
) {
}
