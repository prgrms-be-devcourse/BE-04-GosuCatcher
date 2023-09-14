package com.foo.gosucatcher.domain.search.application.dto.response;

public record SearchRankingResponse(
	int rating,
	String keyword
) {

	public static SearchRankingResponse of(int rating, String keyword) {
		return new SearchRankingResponse(rating, keyword);
	}
}
