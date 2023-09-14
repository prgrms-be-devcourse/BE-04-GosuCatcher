package com.foo.gosucatcher.domain.search.application.dto.response;

import java.util.ArrayList;
import java.util.List;

public record SearchRankingListResponse(
	List<SearchRankingResponse> searchRankingList
) {

	public static SearchRankingListResponse from(List<String> searchRankingList) {
		List<SearchRankingResponse> responses = new ArrayList<>();

		int rating = 1;
		for (String keyword : searchRankingList) {
			responses.add(new SearchRankingResponse(rating, keyword));
			rating++;
		}
		return new SearchRankingListResponse(responses);
	}
}
