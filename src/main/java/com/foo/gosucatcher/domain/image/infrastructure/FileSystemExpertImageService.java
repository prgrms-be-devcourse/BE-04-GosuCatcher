package com.foo.gosucatcher.domain.image.infrastructure;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.expert.domain.ExpertImage;
import com.foo.gosucatcher.domain.expert.domain.ExpertImageRepository;
import com.foo.gosucatcher.domain.expert.domain.ExpertRepository;
import com.foo.gosucatcher.domain.expert.exception.ExpertImageIOException;
import com.foo.gosucatcher.domain.image.ImageService;
import com.foo.gosucatcher.domain.image.application.dto.request.ImageUploadRequest;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.BusinessException;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;
import com.foo.gosucatcher.global.error.exception.InvalidValueException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FileSystemExpertImageService implements ImageService {

	private final ExpertImageRepository expertImageRepository;
	private final ExpertRepository expertRepository;

	// @Value("${spring.servlet.multipart.location}")
	private String uploadPath;

	@Override
	public String store(ImageUploadRequest request) {
		try {
			MultipartFile file = request.file();
			if (file.isEmpty()) {
				throw new InvalidValueException(ErrorCode.INVALID_IMAGE);
			}

			Path root = Paths.get(uploadPath, request.id().toString());
			if (!Files.exists(root)) {
				Files.createDirectories(root);
			}

			String originalFilename = file.getOriginalFilename();
			String extension = FilenameUtils.getExtension(originalFilename);
			String newFilename = UUID.randomUUID() + "." + extension;

			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, root.resolve(newFilename), StandardCopyOption.REPLACE_EXISTING);
			}

			Expert expert = expertRepository.findById(request.id())
				.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_EXPERT));

			ExpertImage expertImage = ExpertImage.builder()
				.filename(newFilename)
				.url(root.resolve(newFilename).toString())
				.size(file.getSize())
				.expert(expert)
				.build();

			expertImageRepository.save(expertImage);
			return newFilename;
		} catch (EntityNotFoundException e) {
			throw e;
		} catch (Exception e) {
			throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public Stream<Path> loadAll(Long expertId) {
		try {
			Path root = Paths.get(uploadPath, expertId.toString());
			return Files.walk(root, 1)
				.filter(path -> !path.equals(root));
		} catch (IOException e) {
			throw new ExpertImageIOException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public Path load(Long expertId, String filename) {
		return Paths.get(uploadPath, expertId.toString()).resolve(filename);
	}

	@Override
	public Resource loadAsResource(Long expertId, String filename) {
		try {
			Path file = load(expertId, filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new ExpertImageIOException(ErrorCode.INVALID_IMAGE);
			}
		} catch (MalformedURLException e) {
			throw new ExpertImageIOException(ErrorCode.INVALID_IMAGE);
		}
	}

	@Override
	public void delete(Long expertId, String filename) {
		try {
			Path root = Paths.get(uploadPath, expertId.toString());

			if (!Files.exists(root)) {
				throw new EntityNotFoundException(ErrorCode.NOT_FOUND_IMAGE);
			}

			Optional<ExpertImage> expertImage = expertImageRepository.findByFilename(filename);

			if (expertImage.isPresent()) {
				expertImageRepository.delete(expertImage.get());
			} else {
				throw new EntityNotFoundException(ErrorCode.NOT_FOUND_IMAGE);
			}

			Files.delete(root.resolve(filename));
		} catch (EntityNotFoundException e) {
			throw e;
		} catch (Exception e) {
			throw new ExpertImageIOException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}
}
