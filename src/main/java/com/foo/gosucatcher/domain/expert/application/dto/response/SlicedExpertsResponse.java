package com.foo.gosucatcher.domain.expert.application.dto.response;

import java.util.List;

import org.springframework.data.domain.Slice;

import com.foo.gosucatcher.domain.expert.domain.Expert;

public record SlicedExpertsResponse(
	List<ExpertResponse> expertsResponse,
	boolean hasNext
) {

	public static SlicedExpertsResponse from(Slice<Expert> experts) {
		return new SlicedExpertsResponse(
			experts.stream()
				.map(ExpertResponse::from)
				.toList(),
			experts.hasNext()
		);
	}
}
