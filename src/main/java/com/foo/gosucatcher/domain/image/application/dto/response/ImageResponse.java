package com.foo.gosucatcher.domain.image.application.dto.response;

public record ImageResponse(
	String filename,
	String url,
	Long size
) {
}
