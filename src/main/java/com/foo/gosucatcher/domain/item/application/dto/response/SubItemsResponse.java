package com.foo.gosucatcher.domain.item.application.dto.response;

import java.util.List;

import com.foo.gosucatcher.domain.item.domain.SubItem;

public record SubItemsResponse(
	List<SubItemResponse> subItemsResponse
) {

	public static SubItemsResponse from(List<SubItem> subItemList) {
		return new SubItemsResponse(subItemList.stream()
			.map(SubItemResponse::from)
			.toList());
	}
}
