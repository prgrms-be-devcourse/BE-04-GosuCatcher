package com.foo.gosucatcher.domain.item.application.dto.response;

import java.util.List;

import com.foo.gosucatcher.domain.item.domain.MainItem;

public record MainItemsResponse(
	List<MainItemResponse> mainItemsResponse
) {

	public static MainItemsResponse from(List<MainItem> mainItemList) {
		return new MainItemsResponse(mainItemList.stream()
			.map(MainItemResponse::from).toList());
	}
}