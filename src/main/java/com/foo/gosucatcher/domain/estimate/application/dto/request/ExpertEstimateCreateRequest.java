package com.foo.gosucatcher.domain.estimate.application.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.foo.gosucatcher.domain.estimate.domain.ExpertEstimate;
import com.foo.gosucatcher.domain.estimate.domain.MemberEstimate;
import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.item.domain.SubItem;

public record ExpertEstimateCreateRequest(
	@NotNull(message = "응답할 고객의 요청 견적서 ID를 입력해주세요.")
	Long memberEstimateId,

	@NotNull(message = "제공할 서비스 ID를 입력해주세요.")
	Long subItemId,

	@NotNull(message = "총 비용을 입력하세요.")
	Integer totalCost,

	@NotBlank(message = "견적서를 설명해주세요.")
	@Size(min = 6, message = "견적서에 대한 설명은 6자 이상 적어주세요.")
	String description,

	@NotNull(message = "자주 사용하는 견적서로 등록할 유무를 알려주세요.")
	Boolean isAuto
) {

	public static ExpertEstimate toExpertResponseEstimate(
		ExpertEstimateCreateRequest expertEstimateCreateRequest,
		MemberEstimate memberEstimate,
		Expert expert, SubItem subItem) {

		return ExpertEstimate.builder()
			.memberEstimate(memberEstimate)
			.subItem(subItem)
			.expert(expert)
			.totalCost(expertEstimateCreateRequest.totalCost)
			.description(expertEstimateCreateRequest.description)
			.isAuto(expertEstimateCreateRequest.isAuto)
			.build();
	}
}
