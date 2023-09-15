package com.foo.gosucatcher.domain.image.application.dto.response;

import java.util.List;

public record ImagesResponse(

	List<String> filenames
) {

	public static ImagesResponse from(List<String> filenames) {
		return new ImagesResponse(filenames);
	}
}
