package com.foo.gosucatcher.domain.review.application.dto.response;

import java.time.LocalDateTime;

import com.foo.gosucatcher.domain.review.domain.Reply;

public record ReplyResponse(
	Long id,
	Long reviewId,
	String content,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {

	public static ReplyResponse of(long reviewId, Reply reply) {
		return new ReplyResponse(reply.getId(), reviewId, reply.getContent(), reply.getCreatedAt(),
			reply.getUpdatedAt());
	}
}
