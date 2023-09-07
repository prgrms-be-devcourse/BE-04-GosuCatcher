package com.foo.gosucatcher.domain.estimate.application.dto.response;

import java.util.List;

import com.foo.gosucatcher.domain.estimate.domain.ExpertEstimate;

public record ExpertEstimatesResponse(
	List<ExpertEstimateResponse> expertResponseEstimatesResponse
) {

	public static ExpertEstimatesResponse from(
		List<ExpertEstimate> expertEstimateList) {
		return new ExpertEstimatesResponse(expertEstimateList.stream()
			.map(ExpertEstimateResponse::from)
			.toList());
	}
}
