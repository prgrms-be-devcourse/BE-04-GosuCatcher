package com.foo.gosucatcher.domain.search.application.dto.response;

import java.util.List;

public record SearchListResponse(
	List<SearchResponse> searchResponseList
) {

	public static SearchListResponse from(List<String> range) {
		List<SearchResponse> searchResponses = range.stream()
			.map(SearchResponse::from)
			.toList();

		return new SearchListResponse(searchResponses);
	}
}
