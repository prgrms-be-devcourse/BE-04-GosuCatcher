package com.foo.gosucatcher.domain.estimate.application.dto.response;

import com.foo.gosucatcher.domain.estimate.domain.ExpertResponseEstimate;

public record ExpertResponseEstimateResponse(
	Long id,
	Long expertId,
	Long memberRequestEstimateId,
	int totalCost,
	String description,
	boolean isOftenUsed
) {

	public static ExpertResponseEstimateResponse from(ExpertResponseEstimate expertResponseEstimate) {
		return new ExpertResponseEstimateResponse(expertResponseEstimate.getId(),
			expertResponseEstimate.getExpert().getId(), expertResponseEstimate.getMemberRequestEstimate().getId(),
			expertResponseEstimate.getTotalCost(), expertResponseEstimate.getDescription(),
			expertResponseEstimate.isOftenUsed());
	}
}
