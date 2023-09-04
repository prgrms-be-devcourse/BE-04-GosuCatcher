package com.foo.gosucatcher.domain.expert.application.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.member.domain.Member;

public record ExpertCreateRequest(
	@NotBlank(message = "업체명은 필수 입력입니다.")
	@Size(max = 20, message = "업체명은 20자 미만이어야 합니다.")
	String storeName,
	@NotBlank(message = "위치는 필수 입력입니다.")
	String location,
	@NotNull(message = "거리는 필수 입력입니다.")
	@Positive(message = "거리는 양수만 가능합니다.")
	int maxTravelDistance,
	@NotBlank(message = "부가 설명을 적어주세요.")
	String description
) {
	public static Expert toExpert(Member member, ExpertCreateRequest request) {
		return Expert.builder()
			.member(member)
			.storeName(request.storeName())
			.location(request.location())
			.maxTravelDistance(request.maxTravelDistance())
			.description(request.description())
			.build();
	}
}