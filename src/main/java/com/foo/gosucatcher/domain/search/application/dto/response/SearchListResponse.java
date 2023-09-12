package com.foo.gosucatcher.domain.search.application.dto.response;

import java.util.List;

public record SearchListResponse(
	List<SearchResponse> searchResponseList
) {

	public static SearchListResponse from(List<SearchResponse> searchResponses) {
		return new SearchListResponse(searchResponses);
	}
}
