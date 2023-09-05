package com.foo.gosucatcher.domain.review.application.dto.response;

import java.util.List;

import com.foo.gosucatcher.domain.review.domain.Review;

public record ReviewsResponse(
		List<ReviewResponse> reviewResponses
) {

	public static ReviewsResponse from(List<Review> reviews) {
		return new ReviewsResponse(
				reviews.stream()
						.map(ReviewResponse::from)
						.toList());
	}
}
