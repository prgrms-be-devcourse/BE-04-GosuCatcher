package com.foo.gosucatcher.domain.review.application.dto.response;

import java.time.LocalDateTime;

import com.foo.gosucatcher.domain.review.domain.Review;

public record ReviewResponse(
		Long id,
		Long expertId,
		Long writerId,
		Long subItemId,
		String description,
		int rating,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
) {

	public static ReviewResponse from(Review review) {
		return new ReviewResponse(review.getId(), review.getExpert().getId(), review.getMember().getId(),
				review.getSubItem().getId(), review.getDescription(), review.getRating(), review.getCreatedAt(),
				review.getUpdatedAt());
	}
}
