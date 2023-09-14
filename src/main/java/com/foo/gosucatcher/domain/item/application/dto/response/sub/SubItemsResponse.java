package com.foo.gosucatcher.domain.item.application.dto.response.sub;

import java.util.List;
import java.util.stream.Collectors;

import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.expert.domain.ExpertItem;
import com.foo.gosucatcher.domain.item.domain.SubItem;

public record SubItemsResponse(
	List<SubItemResponse> subItemsResponse
) {

	public static SubItemsResponse from(List<SubItem> subItemList) {
		return new SubItemsResponse(subItemList.stream()
			.map(SubItemResponse::from)
			.toList());
	}

	public static SubItemsResponse from(Expert expert) {
		List<SubItem> subItemList = expert.getExpertItemList().stream()
			.map(ExpertItem::getSubItem)
			.collect(Collectors.toList());

		return new SubItemsResponse(subItemList.stream()
			.map(SubItemResponse::from)
			.toList());
	}
}
