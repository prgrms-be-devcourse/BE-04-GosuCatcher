package com.foo.gosucatcher.domain.estimate.application.dto.response;

import java.util.List;

import com.foo.gosucatcher.domain.estimate.domain.MemberRequestEstimate;

public record MemberRequestEstimatesResponse(
	List<MemberRequestEstimateResponse> memberRequestEstimates
) {

	public static MemberRequestEstimatesResponse from(List<MemberRequestEstimate> memberRequestEstimates) {
		return new MemberRequestEstimatesResponse(
			memberRequestEstimates.stream().map(MemberRequestEstimateResponse::from).toList());
	}
}
