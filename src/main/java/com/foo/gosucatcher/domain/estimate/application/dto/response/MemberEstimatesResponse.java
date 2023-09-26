package com.foo.gosucatcher.domain.estimate.application.dto.response;

import java.util.List;

import com.foo.gosucatcher.domain.estimate.domain.MemberEstimate;

public record MemberEstimatesResponse(
	List<MemberEstimateResponse> memberEstimates
) {

	public static MemberEstimatesResponse from(List<MemberEstimate> memberEstimates) {

		return new MemberEstimatesResponse(
			memberEstimates.stream().map(MemberEstimateResponse::from).toList());
	}
}
