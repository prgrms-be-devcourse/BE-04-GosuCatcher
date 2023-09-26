package com.foo.gosucatcher.domain.image;

import com.foo.gosucatcher.domain.image.application.dto.request.ImageDeleteRequest;
import com.foo.gosucatcher.domain.image.application.dto.request.ImageUploadRequest;
import com.foo.gosucatcher.domain.image.application.dto.response.ImagesResponse;

public interface ImageService {

	ImagesResponse store(ImageUploadRequest request);

	void delete(ImageDeleteRequest request);
}
