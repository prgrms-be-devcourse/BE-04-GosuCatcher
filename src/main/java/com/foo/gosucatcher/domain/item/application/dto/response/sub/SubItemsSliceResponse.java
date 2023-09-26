package com.foo.gosucatcher.domain.item.application.dto.response.sub;

import java.util.List;

import org.springframework.data.domain.Slice;

import com.foo.gosucatcher.domain.item.domain.SubItem;

public record SubItemsSliceResponse(
	List<SubItemSliceResponse> subItemSlicesResponse,
	boolean hasNext
) {

	public static SubItemsSliceResponse from(Slice<SubItem> subItemSlice) {
		List<SubItemSliceResponse> subItemSliceResponses = subItemSlice.stream()
			.map(SubItemSliceResponse::from)
			.toList();

		return new SubItemsSliceResponse(subItemSliceResponses, subItemSlice.hasNext());
	}
}
