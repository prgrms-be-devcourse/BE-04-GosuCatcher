package com.foo.gosucatcher.domain.estimate.application.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.foo.gosucatcher.domain.estimate.domain.ExpertEstimate;
import com.foo.gosucatcher.domain.estimate.domain.MemberEstimate;
import com.foo.gosucatcher.domain.expert.domain.Expert;

public record ExpertNormalEstimateCreateRequest(
	@NotNull(message = "총 비용을 입력하세요.")
	Integer totalCost,

	@NotBlank(message = "활동 가능한 지역을 입력해주세요.")
	String activityLocation,

	@NotBlank(message = "견적서를 설명해주세요.")
	@Size(min = 6, message = "견적서에 대한 설명은 6자 이상 적어주세요.")
	String description
) {

	public static ExpertEstimate toExpertEstimate(ExpertNormalEstimateCreateRequest expertNormalEstimateCreateRequest, MemberEstimate memberEstimate, Expert expert) {
		ExpertEstimate expertEstimate = ExpertEstimate.builder()
			.memberEstimate(memberEstimate)
			.expert(expert)
			.totalCost(expertNormalEstimateCreateRequest.totalCost)
			.activityLocation(expertNormalEstimateCreateRequest.activityLocation)
			.description(expertNormalEstimateCreateRequest.description)
			.build();

		memberEstimate.addExpertEstimate(expertEstimate);
		expertEstimate.addSubItem(memberEstimate.getSubItem());

		return expertEstimate;
	}
}
