package com.foo.gosucatcher.domain.image.infrastructure;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.foo.gosucatcher.domain.image.ImageService;
import com.foo.gosucatcher.domain.image.application.dto.request.ImageUploadRequest;
import com.foo.gosucatcher.domain.image.application.dto.response.ImageResponse;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
@Qualifier("aws")
public class S3ImageService implements ImageService {

	private final AmazonS3Client amazonS3Client;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	@Value("${cloud.aws.s3.directory}")
	private String directory;

	@Override
	public String store(Long memberId, ImageUploadRequest request) {
	}

	@Override
	public List<ImageResponse> loadAll(Long memberId) {
		return null;
	}

	@Override
	public Path load(Long memberId, String filename) {
		return null;
	}

	@Override
	public Resource loadAsResource(Long memberId, String filename) {
		return null;
	}

	@Override
	public void delete(Long memberId, String filename) {

	}

	private Optional<File> convert(MultipartFile file) throws IOException {
		File convertFile = new File(file.getOriginalFilename());
		if (convertFile.createNewFile()) {
			try (FileOutputStream fos = new FileOutputStream(convertFile)) {
				fos.write(file.getBytes());
			}
			return Optional.of(convertFile);
		}
		return Optional.empty();
	}
}
