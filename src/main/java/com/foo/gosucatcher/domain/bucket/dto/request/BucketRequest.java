package com.foo.gosucatcher.domain.bucket.dto.request;

import javax.validation.constraints.NotNull;

import com.foo.gosucatcher.domain.bucket.domain.Bucket;
import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.member.domain.Member;

public record BucketRequest(

	@NotNull(message = "찜하고자 하는 고수의 아이디를 입력해주세요.")
	Long expertId,

	@NotNull(message = "사용자 아이디를 입력해주세요.")
	Long memberId
) {

	public static Bucket toLikes(Member member, Expert expert) {
		return Bucket.builder()
			.member(member)
			.expert(expert)
			.build();
	}
}
