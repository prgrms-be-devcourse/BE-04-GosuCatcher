package com.foo.gosucatcher.domain.search.application.dto.response;

import java.util.ArrayList;
import java.util.List;

public record SearchRankingListResponse(
	List<SearchRankingResponse> searchRankingList
) {

	public static SearchRankingListResponse from(List<String> searchRankingList) {
		List<SearchRankingResponse> rankingListResponse = new ArrayList<>();

		int rating = 1;
		for (String keyword : searchRankingList) {
			SearchRankingResponse searchRankingResponse = SearchRankingResponse.of(rating, keyword);
			rankingListResponse.add(searchRankingResponse);
			rating++;
		}
		return new SearchRankingListResponse(rankingListResponse);
	}
}
