package com.foo.gosucatcher.domain.image;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;

import com.foo.gosucatcher.domain.image.application.dto.request.ImageUploadRequest;

public interface ImageService {

	String store(ImageUploadRequest request);

	Stream<Path> loadAll(Long id);

	Path load(Long id, String filename);

	Resource loadAsResource(Long id, String filename);

	void delete(Long id, String filename);
}
