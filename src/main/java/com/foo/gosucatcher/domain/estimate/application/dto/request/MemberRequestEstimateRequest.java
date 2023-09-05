package com.foo.gosucatcher.domain.estimate.application.dto.request;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.foo.gosucatcher.domain.estimate.domain.MemberRequestEstimate;
import com.foo.gosucatcher.domain.item.domain.SubItem;
import com.foo.gosucatcher.domain.member.domain.Member;

public record MemberRequestEstimateRequest(
	@NotNull(message = "세부 서비스 id를 등록해주세요.")
	Long subItemId,

	@NotBlank(message = "지역을 등록해주세요.")
	String location,

	@NotNull(message = "서비스 희망일을 등록해주세요.")
	LocalDateTime preferredStartDate,

	@Size(max = 500, message = "상세 설명은 500자 이하로 등록 가능합니다.")
	String detailedDescription
) {

	public static MemberRequestEstimate toMemberRequestEstimate(Member member, SubItem subItem,
		MemberRequestEstimateRequest memberRequestEstimateRequest) {
		
		return MemberRequestEstimate.builder()
			.member(member)
			.subItem(subItem)
			.location(memberRequestEstimateRequest.location)
			.preferredStartDate(memberRequestEstimateRequest.preferredStartDate)
			.detailedDescription(memberRequestEstimateRequest.detailedDescription)
			.build();
	}
}
