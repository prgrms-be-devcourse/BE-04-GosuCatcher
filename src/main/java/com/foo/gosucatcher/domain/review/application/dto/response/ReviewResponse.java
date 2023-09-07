package com.foo.gosucatcher.domain.review.application.dto.response;

import java.time.LocalDateTime;
import java.util.Dictionary;
import java.util.Hashtable;

import com.foo.gosucatcher.domain.review.domain.Review;

public record ReviewResponse(
	Long id,
	Long expertId,
	Long writerId,
	Long subItemId,
	String content,
	int rating,
	boolean replyExisted,
	Dictionary<String, String> reply,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {

	public static ReviewResponse from(Review review) {
		Dictionary<String, String> reply = new Hashtable<>();
		boolean replyExisted = review.getReply() != null;

		if (replyExisted) {
			reply.put("id", review.getReply().getId().toString());
			reply.put("content", review.getReply().getContent());
			reply.put("createdAt", review.getReply().getCreatedAt().toString());
			reply.put("UpdatedAt", review.getReply().getUpdatedAt().toString());
		}

		return new ReviewResponse(review.getId(), review.getExpert().getId(), review.getMember().getId(),
			review.getSubItem().getId(), review.getContent(), review.getRating(), replyExisted,
			reply, review.getCreatedAt(), review.getUpdatedAt());
	}
}
