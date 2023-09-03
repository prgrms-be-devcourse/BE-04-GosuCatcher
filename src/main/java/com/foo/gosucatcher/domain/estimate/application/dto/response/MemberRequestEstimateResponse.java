package com.foo.gosucatcher.domain.estimate.application.dto.response;

import java.time.LocalDateTime;

import com.foo.gosucatcher.domain.estimate.domain.MemberRequestEstimate;

public record MemberRequestEstimateResponse(
	Long id,
	Long memberId,
	Long subItemId,
	String location,
	LocalDateTime startDate,
	String detailedDescription
) {

	public static MemberRequestEstimateResponse from(MemberRequestEstimate memberRequestEstimate) {
		return new MemberRequestEstimateResponse(memberRequestEstimate.getId(),
			memberRequestEstimate.getMember().getId(), memberRequestEstimate.getSubItem().getId(),
			memberRequestEstimate.getLocation(), memberRequestEstimate.getStartDate(),
			memberRequestEstimate.getDetailedDescription());
	}
}
