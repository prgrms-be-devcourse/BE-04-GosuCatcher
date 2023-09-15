package com.foo.gosucatcher.domain.image.application.dto.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public record ImageUploadRequest(

	List<MultipartFile> files
) {
}
