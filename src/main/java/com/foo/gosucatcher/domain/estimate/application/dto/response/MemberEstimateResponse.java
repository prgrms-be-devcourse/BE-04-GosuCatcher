package com.foo.gosucatcher.domain.estimate.application.dto.response;

import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.foo.gosucatcher.domain.estimate.domain.MemberEstimate;
import com.foo.gosucatcher.domain.expert.domain.Expert;

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

		Long expertIdFromMemberEstimate = null;

		if (memberEstimate.getExpert() != null) {
			expertIdFromMemberEstimate = memberEstimate.getExpert().getId();
		}

		return new MemberEstimateResponse(memberEstimate.getId(),
			memberEstimate.getMember().getId(), expertIdFromMemberEstimate,
			memberEstimate.getSubItem().getId(),
			memberEstimate.getLocation(), memberEstimate.getPreferredStartDate(),
			memberEstimate.getDetailedDescription());
	}
}
