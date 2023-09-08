package com.foo.gosucatcher.domain.image;

import java.nio.file.Path;
import java.util.List;

import org.springframework.core.io.Resource;

import com.foo.gosucatcher.domain.image.application.dto.request.ImageUploadRequest;
import com.foo.gosucatcher.domain.image.application.dto.response.ImageResponse;

public interface ImageService {

	String store(ImageUploadRequest request);

	List<ImageResponse> loadAll(Long id);

	Path load(Long id, String filename);

	Resource loadAsResource(Long id, String filename);

	void delete(Long id, String filename);
}
