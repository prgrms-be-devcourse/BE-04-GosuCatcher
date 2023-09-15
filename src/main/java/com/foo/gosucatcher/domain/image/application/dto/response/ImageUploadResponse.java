package com.foo.gosucatcher.domain.image.application.dto.response;

import java.util.List;

public record ImageUploadResponse(

	List<String> filenames
) {

	public static ImageUploadResponse from(List<String> filenames) {
		return new ImageUploadResponse(filenames);
	}
}
