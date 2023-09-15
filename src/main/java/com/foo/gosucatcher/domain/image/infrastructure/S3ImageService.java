package com.foo.gosucatcher.domain.image.infrastructure;

import static com.foo.gosucatcher.global.error.ErrorCode.INTERNAL_SERVER_ERROR;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.foo.gosucatcher.domain.image.ImageService;
import com.foo.gosucatcher.domain.image.application.dto.request.ImageDeleteRequest;
import com.foo.gosucatcher.domain.image.application.dto.request.ImageUploadRequest;
import com.foo.gosucatcher.domain.image.application.dto.response.ImageUploadResponse;
import com.foo.gosucatcher.global.error.exception.ImageIOException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
@Qualifier("aws")
public class S3ImageService implements ImageService {

	private final AmazonS3Client amazonS3Client;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	// @Value("${cloud.aws.s3.directory}")
	// private String directoryName;

	@Override
	public ImageUploadResponse store(ImageUploadRequest request) {
		List<String> paths = new ArrayList<>();

		try {
			for (MultipartFile multipartFile : request.files()) {
				File file = convertToFile(multipartFile);

				String path = store(file);
				paths.add(path);
			}
		} catch (IOException exception) {
			throw new ImageIOException(INTERNAL_SERVER_ERROR);
		}

		return ImageUploadResponse.from(paths);
	}

	@Override
	public void delete(ImageDeleteRequest request) {
		for (String fileName : request.filenames()) {
			DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, fileName);
			amazonS3Client.deleteObject(deleteObjectRequest);
		}
	}

	private String store(File uploadFile) {
		String fileName = uploadFile.getName();
		String uploadImageUrl = putS3(uploadFile, fileName);

		removeTemporaryFile(uploadFile);

		return uploadImageUrl;
	}

	private String putS3(File uploadFile, String fileName) {
		amazonS3Client.putObject(
			new PutObjectRequest(bucket, fileName, uploadFile)
				.withCannedAcl(CannedAccessControlList.PublicRead)
		);
		return amazonS3Client.getUrl(bucket, fileName).toString();
	}

	private void removeTemporaryFile(File targetFile) {
		boolean result = targetFile.delete();

		if (!result) {
			throw new ImageIOException(INTERNAL_SERVER_ERROR);
		}
	}

	private File convertToFile(MultipartFile file) throws IOException {
		File convertFile = new File(getFileName());
		if (convertFile.createNewFile()) {
			try (FileOutputStream fileOutputStream = new FileOutputStream(convertFile)) {
				fileOutputStream.write(file.getBytes());
			}
			return convertFile;
		}
		throw new ImageIOException(INTERNAL_SERVER_ERROR);
	}

	private String getFileName() {
		StringBuilder fileName = new StringBuilder();

		LocalDateTime now = LocalDateTime.now();
		fileName.append(now.format(DateTimeFormatter.ofPattern("yy/MM/dd/")));

		fileName.append(UUID.randomUUID());

		return fileName.toString();
	}

}
