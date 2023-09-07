package com.foo.gosucatcher.domain.image.application.dto.request;

import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

public record ImageUploadRequest(

	@NotNull
	Long id,
	MultipartFile file
) {
}
