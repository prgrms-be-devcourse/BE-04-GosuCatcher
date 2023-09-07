package com.foo.gosucatcher.domain.item.application.dto.response.sub;

import com.foo.gosucatcher.domain.item.domain.SubItem;

import java.util.List;

public record SubItemsResponse(
    List<SubItemResponse> subItemsResponse
) {

    public static SubItemsResponse from(List<SubItem> subItemList) {
        return new SubItemsResponse(subItemList.stream()
            .map(SubItemResponse::from)
            .toList());
    }
}
