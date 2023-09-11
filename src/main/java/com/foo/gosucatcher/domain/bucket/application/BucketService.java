package com.foo.gosucatcher.domain.bucket.application;

import static com.foo.gosucatcher.global.error.ErrorCode.NOT_FOUND_BUCKET;
import static com.foo.gosucatcher.global.error.ErrorCode.NOT_FOUND_EXPERT;
import static com.foo.gosucatcher.global.error.ErrorCode.NOT_FOUND_MEMBER;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foo.gosucatcher.domain.bucket.domain.Bucket;
import com.foo.gosucatcher.domain.bucket.domain.BucketRepository;
import com.foo.gosucatcher.domain.bucket.dto.request.BucketRequest;
import com.foo.gosucatcher.domain.bucket.dto.response.BucketResponse;
import com.foo.gosucatcher.domain.bucket.dto.response.BucketsResponse;
import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.expert.domain.ExpertRepository;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.domain.member.domain.MemberRepository;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class BucketService {

	private final BucketRepository bucketRepository;
	private final MemberRepository memberRepository;
	private final ExpertRepository expertRepository;

	@Transactional(readOnly = true)
	public BucketsResponse findAll(Pageable pageable) {
		Slice<Bucket> likes = bucketRepository.findAll(pageable);

		return BucketsResponse.from(likes);
	}

	public void deleteById(Long id) {
		bucketRepository.findById(id)
			.ifPresentOrElse(
				bucket -> bucketRepository.deleteById(id),
				() -> {
					throw new EntityNotFoundException(NOT_FOUND_BUCKET);
				}
			);
	}

	public BucketResponse create(BucketRequest bucketRequest) {
		Member member = memberRepository.findById(bucketRequest.memberId())
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MEMBER));
		Expert expert = expertRepository.findById(bucketRequest.expertId())
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_EXPERT));

		Bucket bucket = BucketRequest.toLikes(member, expert);

		bucketRepository.save(bucket);

		return BucketResponse.from(bucket);
	}

	@Transactional(readOnly = true)
	public Boolean checkStatus(Long expertId, Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MEMBER));
		Expert expert = expertRepository.findById(expertId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_EXPERT));

		return bucketRepository.findByMemberIdAndExpertId(member.getId(), expert.getId())
				.isPresent();
	}
}
