package com.foo.gosucatcher.domain.review.application.dto.response;

import java.util.List;

import com.foo.gosucatcher.domain.review.domain.Review;

public record ReviewResponses(
		List<ReviewResponse> reviewResponses
) {

	public static ReviewResponses from(List<Review> reviews) {
		return new ReviewResponses(
				reviews.stream()
						.map(ReviewResponse::from)
						.toList());
	}
}
