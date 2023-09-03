package com.foo.gosucatcher.domain.item.application.dto.response;

import com.foo.gosucatcher.domain.item.domain.MainItem;

public record MainItemResponse(
	Long id,
	String name,
	String description
) {

	public static MainItemResponse from(MainItem mainItem) {
		return new MainItemResponse(mainItem.getId(), mainItem.getName(), mainItem.getDescription());
	}
}
