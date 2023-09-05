package com.foo.gosucatcher.domain.estimate.application.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.foo.gosucatcher.domain.estimate.domain.ExpertResponseEstimate;
import com.foo.gosucatcher.domain.estimate.domain.MemberRequestEstimate;
import com.foo.gosucatcher.domain.expert.domain.Expert;

public record ExpertResponseEstimateUpdateRequest(
	@NotNull(message = "총 비용을 입력하세요.")
	Integer totalCost,

	@NotBlank(message = "견적서를 설명해주세요.")
	@Size(min = 6, message = "견적서에 대한 설명은 6자 이상 적어주세요.")
	String description,

	@NotNull(message = "자주 사용하는 견적서로 등록할 유무를 알려주세요.")
	Boolean isOftenUsed
) {

	public static ExpertResponseEstimate toExpertResponseEstimate(
		ExpertResponseEstimateUpdateRequest expertResponseEstimateUpdateRequest, Expert expert,
		MemberRequestEstimate memberRequestEstimate
	) {
		return ExpertResponseEstimate.builder()
			.expert(expert)
			.memberRequestEstimate(memberRequestEstimate)
			.totalCost(expertResponseEstimateUpdateRequest.totalCost)
			.description(expertResponseEstimateUpdateRequest.description)
			.isOftenUsed(expertResponseEstimateUpdateRequest.isOftenUsed)
			.build();
	}
}
