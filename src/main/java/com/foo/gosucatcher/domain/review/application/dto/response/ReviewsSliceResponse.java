package com.foo.gosucatcher.domain.review.application.dto.response;

import java.util.List;

import org.springframework.data.domain.Slice;

import com.foo.gosucatcher.domain.review.domain.Review;

public record ReviewsSliceResponse(
	List<ReviewResponse> ReviewsSliceResponse,
	boolean hasNext
) {

	public static ReviewsSliceResponse from(Slice<Review> reviews) {
		return new ReviewsSliceResponse(
			reviews.stream()
				.map(ReviewResponse::from)
				.toList(), reviews.hasNext());
	}
}
