package com.foo.gosucatcher.domain.estimate.application.dto.response;

import com.foo.gosucatcher.domain.estimate.domain.ExpertEstimate;

public record ExpertEstimateResponse(
	Long id,
	Long expertId,
	Long memberRequestEstimateId,
	int totalCost,
	String description,
	boolean isOftenUsed
) {

	public static ExpertEstimateResponse from(ExpertEstimate expertEstimate) {
		return new ExpertEstimateResponse(expertEstimate.getId(),
			expertEstimate.getExpert().getId(), expertEstimate.getMemberRequestEstimate().getId(),
			expertEstimate.getTotalCost(), expertEstimate.getDescription(),
			expertEstimate.isAuto());
	}
}
