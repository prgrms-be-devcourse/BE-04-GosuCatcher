package com.foo.gosucatcher.domain.item.application.dto.response.main;

import com.foo.gosucatcher.domain.item.domain.MainItem;

import java.util.List;

public record MainItemsResponse(
    List<MainItemResponse> mainItemsResponse
) {

    public static MainItemsResponse from(List<MainItem> mainItemList) {
        return new MainItemsResponse(mainItemList.stream()
            .map(MainItemResponse::from)
            .toList());
    }
}
