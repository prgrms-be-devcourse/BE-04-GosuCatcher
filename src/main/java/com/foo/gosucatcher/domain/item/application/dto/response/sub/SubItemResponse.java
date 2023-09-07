package com.foo.gosucatcher.domain.item.application.dto.response.sub;

import com.foo.gosucatcher.domain.item.domain.SubItem;

public record SubItemResponse(
    Long id,
    String mainItemName,
    String name,
    String description
) {

    public static SubItemResponse from(SubItem subItem) {
        return new SubItemResponse(subItem.getId(), subItem.getMainItem().getName(), subItem.getName(),
            subItem.getDescription());
    }
}
