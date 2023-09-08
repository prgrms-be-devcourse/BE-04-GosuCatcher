package com.foo.gosucatcher.domain.estimate.application.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.foo.gosucatcher.domain.estimate.domain.ExpertEstimate;
import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.item.domain.SubItem;

public record ExpertAutoEstimateCreateRequest(
	@NotNull(message = "제공할 서비스 ID를 입력해주세요.")
	Long subItemId,

	@NotNull(message = "총 비용을 입력하세요.")
	Integer totalCost,

	@NotBlank(message = "활동 가능한 지역을 입력해주세요.")
	@Size(min = 3, message = "활동 가능한 지역명을 정확하게 적어주세요.")
	String activityLocation,

	@NotBlank(message = "견적서를 설명해주세요.")
	@Size(min = 6, message = "견적서에 대한 설명은 6자 이상 적어주세요.")
	String description
) {

	public static ExpertEstimate toExpertEstimate(ExpertAutoEstimateCreateRequest expertAutoEstimateCreateRequest,
												  Expert expert, SubItem subItem) {
		return ExpertEstimate.builder()
			.subItem(subItem)
			.expert(expert)
			.totalCost(expertAutoEstimateCreateRequest.totalCost)
			.activityLocation(expertAutoEstimateCreateRequest.activityLocation)
			.description(expertAutoEstimateCreateRequest.description)
			.build();
	}
}
