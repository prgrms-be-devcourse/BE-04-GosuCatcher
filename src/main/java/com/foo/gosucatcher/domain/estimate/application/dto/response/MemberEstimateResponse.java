package com.foo.gosucatcher.domain.estimate.application.dto.response;

import java.time.LocalDateTime;

import com.foo.gosucatcher.domain.estimate.domain.MemberEstimate;

public record MemberEstimateResponse(
	Long id,
	Long memberId,
	Long expertId,
	Long subItemId,
	String location,
	LocalDateTime preferredStartDate,
	String detailedDescription
) {

	public static MemberEstimateResponse from(MemberEstimate memberEstimate) {

		if (memberEstimate == null) {
			return new MemberEstimateResponse(null, null, null, null, null, null, null);
		}

		return new MemberEstimateResponse(memberEstimate.getId(),
			memberEstimate.getMember().getId(), null,
			memberEstimate.getSubItem().getId(),
			memberEstimate.getLocation(), memberEstimate.getPreferredStartDate(),
			memberEstimate.getDetailedDescription());
	}
}
