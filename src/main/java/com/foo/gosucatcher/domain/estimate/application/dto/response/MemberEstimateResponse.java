package com.foo.gosucatcher.domain.estimate.application.dto.response;

import java.time.LocalDateTime;

import com.foo.gosucatcher.domain.estimate.domain.MemberEstimate;
import com.foo.gosucatcher.domain.estimate.domain.Status;
import com.foo.gosucatcher.domain.item.application.dto.response.sub.SubItemResponse;

public record MemberEstimateResponse(
	Long id,
	Long memberId,
	Long expertId,
	SubItemResponse subItemResponse,
	String location,
	LocalDateTime preferredStartDate,
	String detailedDescription,
	Status status
) {

	public static MemberEstimateResponse from(MemberEstimate memberEstimate) {

		if (memberEstimate == null) {
			return new MemberEstimateResponse(null, null, null, null, null, null, null, null);
		}

		Long expertIdFromMemberEstimate = null;

		if (memberEstimate.getExpert() != null) {
			expertIdFromMemberEstimate = memberEstimate.getExpert().getId();
		}

		return new MemberEstimateResponse(memberEstimate.getId(),
			memberEstimate.getMember().getId(), expertIdFromMemberEstimate,
			SubItemResponse.from(memberEstimate.getSubItem()),
			memberEstimate.getLocation(), memberEstimate.getPreferredStartDate(),
			memberEstimate.getDetailedDescription(),
			memberEstimate.getStatus());
	}
}
