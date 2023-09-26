package com.foo.gosucatcher.domain.review.application.dto.response;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.foo.gosucatcher.domain.review.domain.Review;
import com.foo.gosucatcher.domain.review.domain.ReviewImage;

public record ReviewResponse(
	Long id,
	String expert,
	String writer,
	String subItem,
	double rating,
	String content,
	boolean replyExisted,
	Map<String, String> reply,

	List<String> images,

	LocalDateTime updatedAt
) {

	public static ReviewResponse from(Review review) {
		Map<String, String> reply = new HashMap<>();
		boolean replyExisted = review.getReply() != null;

		if (replyExisted) {
			reply.put("expert", review.getExpert().getStoreName());
			reply.put("content", review.getReply().getContent());
			reply.put("updatedAt", review.getReply().getUpdatedAt().toString());
		}

		List<String> reviewImages = List.of();
		if (review.getReviewImages() != null) {
			reviewImages = review.getReviewImages().stream().map(ReviewImage::getPath).toList();
		}

		return new ReviewResponse(review.getId(), review.getExpert().getStoreName(), review.getWriter().getName(),
			review.getSubItem().getName(), review.getRating(), review.getContent(), replyExisted,
			reply, reviewImages, review.getUpdatedAt());
	}
}
