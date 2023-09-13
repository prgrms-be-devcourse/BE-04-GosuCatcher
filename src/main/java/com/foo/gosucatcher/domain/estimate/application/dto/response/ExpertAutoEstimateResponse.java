package com.foo.gosucatcher.domain.estimate.application.dto.response;

import com.foo.gosucatcher.domain.estimate.domain.ExpertEstimate;
import com.foo.gosucatcher.domain.expert.application.dto.response.ExpertResponse;

public record ExpertAutoEstimateResponse(
	Long id,
	ExpertResponse expert,
	Long subItemId,
	int totalCost,
	String activityLocation,
	String description
) {

	public static ExpertAutoEstimateResponse from(ExpertEstimate expertEstimate) {
		return new ExpertAutoEstimateResponse(
			expertEstimate.getId(),
			ExpertResponse.from(expertEstimate.getExpert()),
			expertEstimate.getSubItem().getId(),
			expertEstimate.getTotalCost(),
			expertEstimate.getActivityLocation(),
			expertEstimate.getDescription());
	}
}
