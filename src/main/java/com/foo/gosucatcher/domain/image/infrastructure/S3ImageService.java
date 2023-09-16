package com.foo.gosucatcher.domain.image.infrastructure;

import static com.foo.gosucatcher.global.error.ErrorCode.EMPTY_IMAGE;
import static com.foo.gosucatcher.global.error.ErrorCode.INTERNAL_SERVER_ERROR;
import static com.foo.gosucatcher.global.error.ErrorCode.INVALID_IMAGE_FORMAT;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.foo.gosucatcher.domain.image.ImageService;
import com.foo.gosucatcher.domain.image.application.dto.request.ImageDeleteRequest;
import com.foo.gosucatcher.domain.image.application.dto.request.ImageUploadRequest;
import com.foo.gosucatcher.domain.image.application.dto.response.ImagesResponse;
import com.foo.gosucatcher.domain.image.exception.ImageIOException;
import com.foo.gosucatcher.domain.image.exception.InvalidFileTypeException;
import com.foo.gosucatcher.global.error.exception.InvalidValueException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class S3ImageService implements ImageService {

	private static final String supportedImageExtension[] = {"jpg", "jpeg", "png"};
	private final AmazonS3 amazonS3;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	@Override
	public ImagesResponse store(ImageUploadRequest request) {
		List<String> paths = new ArrayList<>();
		for (MultipartFile multipartFile : request.files()) {
			validateFile(multipartFile);
			File file = convertToFile(multipartFile);

			String path = store(file);
			paths.add(path);
		}

		return ImagesResponse.from(paths);
	}

	@Override
	public void delete(ImageDeleteRequest request) {
		for (String fileName : request.filenames()) {
			DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, fileName);
			amazonS3.deleteObject(deleteObjectRequest);
		}
	}

	private String store(File uploadFile) {
		String uploadImageUrl = putS3(uploadFile, getFileName());

		removeTemporaryFile(uploadFile);

		return uploadImageUrl;
	}

	private String putS3(File uploadFile, String fileName) {
		try {
			amazonS3.putObject(
				new PutObjectRequest(bucket, fileName, uploadFile)
					.withCannedAcl(CannedAccessControlList.PublicRead)
			);
		} catch (SdkClientException e) {
			throw new ImageIOException(INTERNAL_SERVER_ERROR);
		}

		return amazonS3.getUrl(bucket, fileName).toString();
	}

	private void removeTemporaryFile(File targetFile) {
		boolean result = targetFile.delete();

		if (!result) {
			throw new ImageIOException(INTERNAL_SERVER_ERROR);
		}
	}

	private File convertToFile(MultipartFile file) {
		try {
			File convertFile = new File(file.getOriginalFilename());

			if (convertFile.createNewFile()) {
				try (FileOutputStream fileOutputStream = new FileOutputStream(convertFile)) {
					fileOutputStream.write(file.getBytes());
				}
			}
			return convertFile;
		} catch (IOException ioException) {
			throw new ImageIOException(INTERNAL_SERVER_ERROR);
		}
	}

	private String getExtension(MultipartFile file) {
		String fileName = file.getOriginalFilename();
		String extension = fileName.substring(fileName.lastIndexOf(".") + 1);

		return extension;
	}

	private String getFileName() {
		StringBuilder fileName = new StringBuilder();

		LocalDateTime now = LocalDateTime.now();
		fileName.append(now.format(DateTimeFormatter.ofPattern("yy/MM/dd/")));

		fileName.append(UUID.randomUUID());

		return fileName.toString();
	}

	private void validateFile(MultipartFile file) {
		if (file.isEmpty()) {
			throw new InvalidValueException(EMPTY_IMAGE);
		}

		String inputExtension = getExtension(file);
		boolean isExtensionValid = Arrays.stream(supportedImageExtension)
			.anyMatch(extension -> extension.equalsIgnoreCase(inputExtension));

		if (!isExtensionValid) {
			throw new InvalidFileTypeException(INVALID_IMAGE_FORMAT);
		}
	}
}
