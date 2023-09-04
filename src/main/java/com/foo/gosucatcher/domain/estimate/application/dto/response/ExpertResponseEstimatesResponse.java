package com.foo.gosucatcher.domain.estimate.application.dto.response;

import java.util.List;

import com.foo.gosucatcher.domain.estimate.domain.ExpertResponseEstimate;

public record ExpertResponseEstimatesResponse(
	List<ExpertResponseEstimateResponse> expertResponseEstimatesResponse
) {

	public static ExpertResponseEstimatesResponse from(
		List<ExpertResponseEstimate> expertResponseEstimateList) {
		return new ExpertResponseEstimatesResponse(expertResponseEstimateList.stream()
			.map(ExpertResponseEstimateResponse::from)
			.toList());
	}
}
