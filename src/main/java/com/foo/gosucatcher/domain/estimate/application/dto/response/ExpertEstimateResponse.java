package com.foo.gosucatcher.domain.estimate.application.dto.response;

import com.foo.gosucatcher.domain.estimate.domain.ExpertEstimate;
import com.foo.gosucatcher.domain.estimate.domain.MemberEstimate;
import com.foo.gosucatcher.domain.expert.application.dto.response.ExpertResponse;

public record ExpertEstimateResponse(
	Long id,
	ExpertResponse expert,
	MemberEstimateResponse memberEstimate,
	int totalCost,
	String activityLocation,
	String description
) {

	public static ExpertEstimateResponse from(ExpertEstimate expertEstimate) {
		MemberEstimate memberEstimate = expertEstimate.getMemberEstimate() != null ?
			expertEstimate.getMemberEstimate() : null;

		return new ExpertEstimateResponse(
			expertEstimate.getId(),
			ExpertResponse.from(expertEstimate.getExpert()),
			MemberEstimateResponse.from(memberEstimate),
			expertEstimate.getTotalCost(),
			expertEstimate.getActivityLocation(),
			expertEstimate.getDescription());
	}
}
