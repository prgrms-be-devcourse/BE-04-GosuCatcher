package com.foo.gosucatcher.domain.search.application.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record SearchResponse(
	String keyword,
	LocalDate searchTime
) {

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public static SearchResponse from(String keyword) {
		LocalDateTime currentTime = LocalDateTime.now();
		String formattedTime = currentTime.format(formatter);

		return new SearchResponse(keyword, LocalDate.parse(formattedTime, formatter));
	}
}
