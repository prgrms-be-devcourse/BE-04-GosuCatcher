package com.foo.gosucatcher.domain.image.application.dto.request;

import java.util.List;

public record ImageDeleteRequest(

	List<String> filenames
) {
}
