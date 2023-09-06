package com.foo.gosucatcher.domain.bucket.dto.response;

import java.util.List;

import com.foo.gosucatcher.domain.bucket.domain.Bucket;

public record BucketsResponse(
		List<BucketResponse> responses
) {

	public static BucketsResponse from(List<Bucket> bucketList) {
		return new BucketsResponse(bucketList.stream()
				.map(BucketResponse::from)
				.toList());
	}
}
