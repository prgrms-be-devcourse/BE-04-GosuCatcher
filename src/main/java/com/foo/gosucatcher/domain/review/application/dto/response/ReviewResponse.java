package com.foo.gosucatcher.domain.review.application.dto.response;

import java.time.LocalDateTime;

import com.foo.gosucatcher.domain.review.domain.Review;

public record ReviewResponse(
	Long id,
	Long expertId,
	Long writerId,
	Long subItemId,
	String content,
	int rating,
	Long parentId,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {

	public static ReviewResponse from(Review review) {
		if (review == null) {
			return null;
		}

		return new ReviewResponse(review.getId(), review.getExpert().getId(), review.getMember().getId(),
			review.getSubItem().getId(), review.getContent(), review.getRating(),
			review.getParent() != null ? review.getParent().getId() : null,
			review.getCreatedAt(), review.getUpdatedAt());
	}
}
