package com.foo.gosucatcher.domain.bucket.dto.response;

import java.util.List;

import org.springframework.data.domain.Slice;

import com.foo.gosucatcher.domain.bucket.domain.Bucket;

public record BucketsResponse(
	List<BucketResponse> buckets,
	boolean hasNext
) {

	public static BucketsResponse from(Slice<Bucket> bucketList) {
		return new BucketsResponse(bucketList.stream()
			.map(BucketResponse::from)
			.toList(), bucketList.hasNext());
	}
}
