package com.foo.gosucatcher.domain.bucket.dto.request;

import com.foo.gosucatcher.domain.bucket.domain.Bucket;
import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.member.domain.Member;

public record BucketRequest(
		Long expertId,
		Long memberId
) {

	public static Bucket toLikes(Member member, Expert expert) {
		return Bucket.builder()
				.member(member)
				.expert(expert)
				.build();
	}
}
