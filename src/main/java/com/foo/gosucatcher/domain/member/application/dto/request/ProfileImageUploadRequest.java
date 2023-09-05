package com.foo.gosucatcher.domain.member.application.dto.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

import org.springframework.web.multipart.MultipartFile;

public record ProfileImageUploadRequest(
	@Min(value = 0)
	long memberId,
	MultipartFile file
) {
}
