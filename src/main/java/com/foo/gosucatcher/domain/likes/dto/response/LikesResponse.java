package com.foo.gosucatcher.domain.likes.dto.response;

import com.foo.gosucatcher.domain.likes.domain.Likes;

public record LikesResponse(
		Long id,
		Long expertId,
		Long memberId
) {

	public static LikesResponse from(Likes likes) {
		return new LikesResponse(likes.getId(), likes.getExpert().getId(), likes.getMember().getId());
	}
}
