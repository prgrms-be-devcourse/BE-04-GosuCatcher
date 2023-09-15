package com.foo.gosucatcher.domain.image.infrastructure;

import org.springframework.stereotype.Service;

import com.foo.gosucatcher.domain.image.ImageService;
import com.foo.gosucatcher.domain.image.application.dto.request.ImageDeleteRequest;
import com.foo.gosucatcher.domain.image.application.dto.request.ImageUploadRequest;
import com.foo.gosucatcher.domain.image.application.dto.response.ImageUploadResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FileSystemExpertImageService implements ImageService {

	public ImageUploadResponse store(ImageUploadRequest request) {
		return null;
	}

	public void delete(ImageDeleteRequest request) {

	}
}
