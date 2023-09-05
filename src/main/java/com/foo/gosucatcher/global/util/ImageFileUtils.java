package com.foo.gosucatcher.global.util;

import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.UrlResource;
import org.springframework.web.util.UriUtils;

import com.foo.gosucatcher.domain.member.domain.ImageFile;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.InvalidValueException;

public class ImageFileUtils {

	public static UrlResource makeImageFileUrlResource(ImageFile imageFile) {
		String path = imageFile.getPath();
		try {
			return new UrlResource("file:" + path);
		} catch (MalformedURLException e) {
			throw new InvalidValueException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	public static String makeImageFileContentDisposition(ImageFile imageFile) {
		String fileName = imageFile.getFileName() + "." + imageFile.getFileExtension();
		String encodedOriginalFileName = UriUtils.encode(fileName, StandardCharsets.UTF_8);

		return "attachment; filename=\"" + encodedOriginalFileName + "\"";
	}
}
