package com.foo.gosucatcher.domain.expert.application.dto.response;

import java.util.List;

import com.foo.gosucatcher.domain.expert.domain.Expert;

public record ExpertsResponse(
	List<ExpertResponse> expertsResponse
) {

	public static ExpertsResponse from(List<Expert> expertList) {
		return new ExpertsResponse(
			expertList.stream()
				.map(ExpertResponse::from)
				.toList()
		);
	}
}