package com.foo.gosucatcher.domain.review.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.expert.domain.ExpertRepository;
import com.foo.gosucatcher.domain.item.domain.SubItem;
import com.foo.gosucatcher.domain.item.domain.SubItemRepository;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.domain.member.domain.MemberRepository;
import com.foo.gosucatcher.domain.review.application.dto.request.ReviewRequest;
import com.foo.gosucatcher.domain.review.application.dto.response.ReviewResponse;
import com.foo.gosucatcher.domain.review.domain.Review;
import com.foo.gosucatcher.domain.review.domain.ReviewRepository;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final ExpertRepository expertRepository;
	private final MemberRepository memberRepository;
	private final SubItemRepository subItemRepository;

	public ReviewResponse create(ReviewRequest reviewRequest) {
		Expert expert = expertRepository.findById(reviewRequest.expertId())
				.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_EXPERT));
		Member writer = memberRepository.findById(reviewRequest.writerId())
				.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_EXPERT));
		SubItem subItem = subItemRepository.findById(reviewRequest.subItemId())
				.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_SUB_ITEM));

		Review review = ReviewRequest.toReview(reviewRequest, expert, writer, subItem);
		reviewRepository.save(review);

		return ReviewResponse.from(review);
	}

	@Transactional(readOnly = true)
	public ReviewResponse findByExpertId(Long expertId) {
		Review review = reviewRepository.findReviewsByExpertId(expertId)
				.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_REVIEW));

		return ReviewResponse.from(review);
	}
}
