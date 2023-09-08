package com.foo.gosucatcher.domain.estimate.application.dto.response;

import com.foo.gosucatcher.domain.estimate.domain.ExpertEstimate;
import com.foo.gosucatcher.domain.expert.application.dto.response.ExpertResponse;

public record ExpertAutoEstimatesResponse(
	Long id,
	ExpertResponse expert,
	Long subItemId,
	int totalCost,
	String activityLocation,
	String description
) {

	public static ExpertAutoEstimatesResponse from(ExpertEstimate expertEstimate) {
		return new ExpertAutoEstimatesResponse(
			expertEstimate.getId(),
			ExpertResponse.from(expertEstimate.getExpert()),
			expertEstimate.getSubItem().getId(),
			expertEstimate.getTotalCost(),
			expertEstimate.getActivityLocation(),
			expertEstimate.getDescription());
	}
}
