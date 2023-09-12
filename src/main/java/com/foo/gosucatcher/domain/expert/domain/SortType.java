package com.foo.gosucatcher.domain.expert.domain;

import java.util.Arrays;

public enum SortType {
	REVIEW_COUNT("reviewCount"),
	RATING("rating");

	private String columnName;

	SortType(String columnName) {
		this.columnName = columnName;
	}

	public static boolean isValidColumnName(String input) {
		return Arrays.stream(SortType.values())
			.map(SortType::getColumnName)
			.anyMatch(columnName -> columnName.equals(input));
	}

	public String getColumnName() {
		return columnName;
	}
}
