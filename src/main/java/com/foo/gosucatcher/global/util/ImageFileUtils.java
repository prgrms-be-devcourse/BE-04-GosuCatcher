package com.foo.gosucatcher.global.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.BusinessException;
import com.foo.gosucatcher.global.error.exception.InvalidValueException;

public class ImageFileUtils {

	// public static UrlResource makeImageFileUrlResource(MemberImage memberImage) {
	// 	String path = memberImage.getPath();
	// 	try {
	// 		return new UrlResource("file:" + path);
	// 	} catch (MalformedURLException e) {
	// 		throw new InvalidValueException(ErrorCode.INTERNAL_SERVER_ERROR);
	// 	}
	// }
	//
	// public static String makeImageFileContentDisposition(MemberImage memberImage) {
	// 	String fileName = memberImage.getFileName() + "." + memberImage.getFileExtension();
	// 	String encodedOriginalFileName = UriUtils.encode(fileName, StandardCharsets.UTF_8);
	//
	// 	return "attachment; filename=\"" + encodedOriginalFileName + "\"";
	// }

	public static MultipartFile validateFile(MultipartFile file) {
		if (file.isEmpty()) {
			throw new InvalidValueException(ErrorCode.INVALID_IMAGE);
		}

		return file;
	}

	public static Path ensureDirectoryExists(String uploadPath, Long id) {
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

	public static String saveFile(MultipartFile file, Path root) {
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
}
