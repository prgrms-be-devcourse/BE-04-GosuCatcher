package com.foo.gosucatcher.domain.search.application.dto.response;

public record SearchResponse(
	String keyword
) {

	public static SearchResponse from(String keyword) {
		return new SearchResponse(keyword);
	}
}
