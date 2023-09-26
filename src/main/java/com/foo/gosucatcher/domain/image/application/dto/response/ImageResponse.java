package com.foo.gosucatcher.domain.image.application.dto.response;

import java.util.List;

public record ImageResponse(
	List<String> filenames
) {
}
