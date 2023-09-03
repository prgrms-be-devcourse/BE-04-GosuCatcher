package com.foo.gosucatcher.domain.expert.application.dto.response;

import com.foo.gosucatcher.domain.expert.domain.Expert;

public record ExpertResponse(
	Long id,
	String storeName,
	String location,
	int distance,
	String description
) {
	public static ExpertResponse from(Expert expert) {
		return new ExpertResponse(
			expert.getId(),
			expert.getStoreName(),
			expert.getLocation(),
			expert.getDistance(),
			expert.getDescription()
		);
	}
}