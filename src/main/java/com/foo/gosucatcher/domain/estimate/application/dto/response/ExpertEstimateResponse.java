package com.foo.gosucatcher.domain.estimate.application.dto.response;

import com.foo.gosucatcher.domain.estimate.domain.ExpertEstimate;

public record ExpertEstimateResponse(
	Long id,
	Long expertId,
	Long memberEstimateId,
	int totalCost,
	String description,
	boolean isOftenUsed
) {

	public static ExpertEstimateResponse from(ExpertEstimate expertEstimate) {
		return new ExpertEstimateResponse(expertEstimate.getId(),
			expertEstimate.getExpert().getId(), expertEstimate.getMemberEstimate().getId(),
			expertEstimate.getTotalCost(), expertEstimate.getDescription(),
			expertEstimate.isAuto());
	}
}
