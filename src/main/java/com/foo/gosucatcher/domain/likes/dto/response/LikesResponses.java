package com.foo.gosucatcher.domain.likes.dto.response;

import java.util.List;

import com.foo.gosucatcher.domain.likes.domain.Likes;

public record LikesResponses(
		List<LikesResponse> likesResponses
) {

	public static LikesResponses from(List<Likes> likesList) {
		return new LikesResponses(likesList.stream()
				.map(LikesResponse::from)
				.toList());
	}
}
