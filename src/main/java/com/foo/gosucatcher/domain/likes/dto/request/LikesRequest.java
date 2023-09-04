package com.foo.gosucatcher.domain.likes.dto.request;

import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.likes.domain.Likes;
import com.foo.gosucatcher.domain.member.domain.Member;

public record LikesRequest(
		Long expertId,
		Long memberId
) {

	public static Likes toLikes(Member member, Expert expert) {
		return Likes.builder()
				.member(member)
				.expert(expert)
				.build();
	}
}
