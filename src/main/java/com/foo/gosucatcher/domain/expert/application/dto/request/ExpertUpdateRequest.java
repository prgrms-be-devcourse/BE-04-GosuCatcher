package com.foo.gosucatcher.domain.expert.application.dto.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import com.foo.gosucatcher.domain.expert.domain.Expert;

public record ExpertUpdateRequest(
	@NotBlank(message = "업체명은 필수 입력입니다.")
	@Size(max = 20, message = "업체명은 20자 미만이어야 합니다.")
	String storeName,

	@NotBlank(message = "위치는 필수 입력입니다.")
	String location,

	@NotNull(message = "거리는 필수 입력입니다.")
	@Positive(message = "거리는 양수만 가능합니다.")
	@Max(value = 999, message = "거리는 최대 999km까지 가능합니다.")
	Integer maxTravelDistance,

	@NotBlank(message = "부가 설명을 적어주세요.")
	String description
) {

	public static Expert toExpert(ExpertUpdateRequest request) {
		return Expert.builder()
			.storeName(request.storeName())
			.location(request.location())
			.maxTravelDistance(request.maxTravelDistance())
			.description(request.description())
			.build();
	}
}
