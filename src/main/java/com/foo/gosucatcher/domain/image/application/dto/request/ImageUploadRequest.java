package com.foo.gosucatcher.domain.image.application.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record ImageUploadRequest(

	Long id,
	MultipartFile file
) {
}
