package com.foo.gosucatcher.domain.member.application.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record ProfileImageUploadRequest(
	long memberId,
	MultipartFile file
) {
}
