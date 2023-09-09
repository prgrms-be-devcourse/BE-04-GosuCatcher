package com.foo.gosucatcher.domain.image.infrastructure;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.expert.domain.ExpertImage;
import com.foo.gosucatcher.domain.expert.domain.ExpertImageRepository;
import com.foo.gosucatcher.domain.expert.domain.ExpertRepository;
import com.foo.gosucatcher.domain.expert.exception.ExpertImageIOException;
import com.foo.gosucatcher.domain.expert.presentation.ExpertController;
import com.foo.gosucatcher.domain.image.ImageService;
import com.foo.gosucatcher.domain.image.application.dto.request.ImageUploadRequest;
import com.foo.gosucatcher.domain.image.application.dto.response.ImageResponse;
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

	private MultipartFile validateFile(MultipartFile file) {
		if (file.isEmpty()) {
			throw new InvalidValueException(ErrorCode.INVALID_IMAGE);
		}

		return file;
	}

	private Path ensureDirectoryExists(Long id) {
		Path root = Paths.get(uploadPath, id.toString());
		if (!Files.exists(root)) {
			try {
				Files.createDirectories(root);
			} catch (IOException e) {
				throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
			}
		}

		return root;
	}

	private String saveFile(MultipartFile file, Path root) {
		String originalFilename = file.getOriginalFilename();
		String extension = FilenameUtils.getExtension(originalFilename);
		String newFilename = UUID.randomUUID() + "." + extension;

		try (InputStream inputStream = file.getInputStream()) {
			Files.copy(inputStream, root.resolve(newFilename), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
		}

		return newFilename;
	}

	private Expert findExpert(Long id) {

		return expertRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_EXPERT));
	}

	private void saveExpertImage(String filename, MultipartFile file, Expert expert, Path root) {
		ExpertImage expertImage = ExpertImage.builder()
			.filename(filename)
			.url(root.resolve(filename).toString())
			.size(file.getSize())
			.expert(expert)
			.build();

		expertImageRepository.save(expertImage);
	}

	private void throwEntityNotFoundException() {
		throw new EntityNotFoundException(ErrorCode.NOT_FOUND_IMAGE);
	}

	@Override
	public String store(ImageUploadRequest request) {
		MultipartFile file = validateFile(request.file());

		Path root = ensureDirectoryExists(request.id());

		String newFilename = saveFile(file, root);

		Expert expert = findExpert(request.id());

		saveExpertImage(newFilename, file, expert, root);

		return newFilename;
	}

	@Override
	public List<ImageResponse> loadAll(Long expertId) {
		try {
			Path root = Paths.get(uploadPath, expertId.toString());
			return Files.walk(root, 1)
				.filter(path -> !path.equals(root))
				.map(path -> {
					String filename = path.getFileName().toString();
					String url = MvcUriComponentsBuilder.fromMethodName(ExpertController.class,
						"getImage", expertId, filename).build().toString();
					Long size;
					try {
						size = Files.size(path);
					} catch (IOException e) {
						size = 0L;
					}
					return new ImageResponse(filename, url, size);
				})
				.collect(Collectors.toList());
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
			}
			throw new ExpertImageIOException(ErrorCode.INVALID_IMAGE);

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

			expertImage.ifPresentOrElse(
				expertImageRepository::delete,
				this::throwEntityNotFoundException
			);

			Files.delete(root.resolve(filename));
		} catch (EntityNotFoundException e) {
			throw e;
		} catch (Exception e) {
			throw new ExpertImageIOException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}
}
