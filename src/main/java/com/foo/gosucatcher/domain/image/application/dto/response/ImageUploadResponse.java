package com.foo.gosucatcher.domain.image.application.dto.response;

import java.util.List;

public record ImageUploadResponse(

	List<String> filenames
) {
}
