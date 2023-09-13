package com.foo.gosucatcher.domain.expert.domain;

import java.util.Arrays;

import org.springframework.data.domain.Sort;

import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.InvalidValueException;

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

	public static void validateSortColumns(Sort sort) {
		for (Sort.Order order : sort) {
			if (!isValidColumnName(order.getProperty())) {
				throw new InvalidValueException(ErrorCode.NOT_FOUND_EXPERT_SORT_TYPE);
			}
		}
	}

	public String getColumnName() {
		return columnName;
	}
}
