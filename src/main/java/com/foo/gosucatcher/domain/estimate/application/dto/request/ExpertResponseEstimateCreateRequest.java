package com.foo.gosucatcher.domain.estimate.application.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.foo.gosucatcher.domain.estimate.domain.ExpertResponseEstimate;
import com.foo.gosucatcher.domain.estimate.domain.MemberRequestEstimate;
import com.foo.gosucatcher.domain.expert.domain.Expert;

public record ExpertResponseEstimateCreateRequest(
	@NotNull(message = "응답할 고객의 요청 견적서 ID를 입력해주세요.")
	Long memberRequestEstimateId,

	@NotNull(message = "총 비용을 입력하세요.")
	Integer totalCost,

	@NotBlank(message = "견적서를 설명해주세요.")
	@Size(min = 6, message = "견적서에 대한 설명은 6자 이상 적어주세요.")
	String description,

	@NotNull(message = "자주 사용하는 견적서로 등록할 유무를 알려주세요.")
	Boolean isOftenUsed
) {

	public static ExpertResponseEstimate toExpertResponseEstimate(
		ExpertResponseEstimateCreateRequest expertResponseEstimateCreateRequest,
		MemberRequestEstimate memberRequestEstimate,
		Expert expert) {

		return ExpertResponseEstimate.builder()
			.memberRequestEstimate(memberRequestEstimate)
			.expert(expert)
			.totalCost(expertResponseEstimateCreateRequest.totalCost)
			.description(expertResponseEstimateCreateRequest.description)
			.isOftenUsed(expertResponseEstimateCreateRequest.isOftenUsed)
			.build();
	}
}
