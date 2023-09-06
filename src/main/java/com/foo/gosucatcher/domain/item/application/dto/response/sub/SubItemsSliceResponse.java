package com.foo.gosucatcher.domain.item.application.dto.response.sub;

import java.util.List;

public record SubItemsSliceResponse(
    List<SubItemSliceResponse> subItemSlicesResponse,
    boolean hasNext
) {

    public static SubItemsSliceResponse of(List<SubItemSliceResponse> subItemSlicesResponse, boolean hasNext) {
        return new SubItemsSliceResponse(subItemSlicesResponse, hasNext);
    }
}
