package com.foo.gosucatcher.domain.item.application.dto.request.sub;

import javax.validation.constraints.Min;

public record SubItemSliceRequest(
    @Min(0)
    int page,
    @Min(0)
    int size
) {
}
