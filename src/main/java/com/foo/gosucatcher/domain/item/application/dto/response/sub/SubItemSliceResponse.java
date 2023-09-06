package com.foo.gosucatcher.domain.item.application.dto.response.sub;

import com.foo.gosucatcher.domain.item.domain.SubItem;

public record SubItemSliceResponse(
    Long id,
    String name,
    String description
) {

    public static SubItemSliceResponse from(SubItem subItem) {
        return new SubItemSliceResponse(subItem.getId(), subItem.getName(), subItem.getDescription());
    }
}
