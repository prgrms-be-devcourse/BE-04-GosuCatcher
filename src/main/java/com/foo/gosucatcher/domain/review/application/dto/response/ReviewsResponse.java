package com.foo.gosucatcher.domain.review.application.dto.response;

import java.util.List;

import org.springframework.data.domain.Slice;

import com.foo.gosucatcher.domain.review.domain.Review;

public record ReviewsResponse(
	List<ReviewResponse> reviews,
	boolean hasNext
) {

	public static ReviewsResponse from(Slice<Review> reviews) {
		return new ReviewsResponse(
			reviews.stream()
				.map(ReviewResponse::from)
				.toList(), reviews.hasNext());
	}
}
