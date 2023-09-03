package com.foo.gosucatcher.domain.item.application.dto.response;

import com.foo.gosucatcher.domain.item.domain.SubItem;

public record SubItemResponse(
	Long id,
	String MainItemName,
	String name,
	String description
) {

	public static SubItemResponse from(SubItem subItem) {
		return new SubItemResponse(subItem.getId(), subItem.getMainItem().getName(), subItem.getName(),
			subItem.getDescription());
	}
}
