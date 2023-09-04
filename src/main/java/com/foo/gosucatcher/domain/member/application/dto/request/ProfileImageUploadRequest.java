package com.foo.gosucatcher.domain.member.application.dto.request;

import javax.validation.constraints.NotEmpty;

import org.springframework.web.multipart.MultipartFile;

public record ProfileImageUploadRequest(
	@NotEmpty long memberId,
	MultipartFile file
) {
}
