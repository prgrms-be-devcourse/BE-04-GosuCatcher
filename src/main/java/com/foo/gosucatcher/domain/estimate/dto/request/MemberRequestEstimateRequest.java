package com.foo.gosucatcher.domain.estimate.dto.request;

import java.time.LocalDateTime;

import com.foo.gosucatcher.domain.estimate.domain.MemberRequestEstimate;
import com.foo.gosucatcher.domain.item.domain.SubItem;
import com.foo.gosucatcher.domain.member.domain.Member;

public record MemberRequestEstimateRequest(
	String location,
	LocalDateTime startDate,
	String detailedDescription) {

	public static MemberRequestEstimate toMemberRequestEstimate(Member member, SubItem subItem,
		MemberRequestEstimateRequest memberRequestEstimateRequest) {
		return MemberRequestEstimate.builder()
			.member(member)
			.subItem(subItem)
			.location(memberRequestEstimateRequest.location)
			.startDate(memberRequestEstimateRequest.startDate)
			.detailedDescription(memberRequestEstimateRequest.detailedDescription)
			.build();
	}
}
