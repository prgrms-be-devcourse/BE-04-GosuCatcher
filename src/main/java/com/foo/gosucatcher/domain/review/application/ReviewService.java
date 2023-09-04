package com.foo.gosucatcher.domain.review.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.expert.domain.ExpertRepository;
import com.foo.gosucatcher.domain.item.domain.SubItem;
import com.foo.gosucatcher.domain.item.domain.SubItemRepository;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.domain.member.domain.MemberRepository;
import com.foo.gosucatcher.domain.review.application.dto.request.ReviewCreateRequest;
import com.foo.gosucatcher.domain.review.application.dto.request.ReviewUpdateRequest;
import com.foo.gosucatcher.domain.review.application.dto.response.ReviewResponse;
import com.foo.gosucatcher.domain.review.application.dto.response.ReviewResponses;
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

	public ReviewResponse create(ReviewCreateRequest reviewCreateRequest) {
		Expert expert = expertRepository.findById(reviewCreateRequest.expertId())
				.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_EXPERT));
		Member writer = memberRepository.findById(reviewCreateRequest.writerId())
				.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_EXPERT));
		SubItem subItem = subItemRepository.findById(reviewCreateRequest.subItemId())
				.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_SUB_ITEM));

		Review review = ReviewCreateRequest.toReview(reviewCreateRequest, expert, writer, subItem);
		reviewRepository.save(review);

		return ReviewResponse.from(review);
	}

	@Transactional(readOnly = true)
	public ReviewResponses findAll() {
		List<Review> reviews = reviewRepository.findAll();

		if (reviews.isEmpty()) {
			throw new EntityNotFoundException(ErrorCode.NOT_FOUND_REVIEW);
		}

		return ReviewResponses.from(reviews);
	}

	@Transactional(readOnly = true)
	public ReviewResponses findByExpertId(Long expertId) {
		List<Review> reviews = reviewRepository.findAllByExpertId(expertId);

		if (reviews.isEmpty()) {
			throw new EntityNotFoundException(ErrorCode.NOT_FOUND_REVIEW);
		}

		return ReviewResponses.from(reviews);
	}

	@Transactional(readOnly = true)
	public ReviewResponse findById(Long id) {
		Review review = reviewRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_REVIEW));

		return ReviewResponse.from(review);
	}

	public Long update(Long id, ReviewUpdateRequest reviewUpdateRequest) {
		Review review = reviewRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_REVIEW));

		Review updatedReview = ReviewUpdateRequest.toReview(reviewUpdateRequest);

		review.update(updatedReview);

		return id;
	}

	public void delete(Long id) {
		if (reviewRepository.findById(id).isEmpty()) {
			throw new EntityNotFoundException(ErrorCode.NOT_FOUND_REVIEW);
		}

		reviewRepository.deleteById(id);
	}

}
