package com.foo.gosucatcher.domain.estimate.application.dto.response;

import java.time.LocalDateTime;

import com.foo.gosucatcher.domain.estimate.domain.MemberEstimate;

public record MemberEstimateResponse(
	Long id,
	Long memberId,
	Long subItemId,
	String location,
	LocalDateTime preferredStartDate,
	String detailedDescription
) {

	public static MemberEstimateResponse from(MemberEstimate memberEstimate) {

		return new MemberEstimateResponse(memberEstimate.getId(),
			memberEstimate.getMember().getId(), memberEstimate.getSubItem().getId(),
			memberEstimate.getLocation(), memberEstimate.getPreferredStartDate(),
			memberEstimate.getDetailedDescription());
	}
}
