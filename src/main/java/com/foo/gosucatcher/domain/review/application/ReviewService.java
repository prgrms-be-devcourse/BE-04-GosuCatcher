package com.foo.gosucatcher.domain.review.application;

import java.util.List;

import org.springframework.data.domain.Pageable;
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
import com.foo.gosucatcher.domain.review.application.dto.response.ReviewsResponse;
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

	public ReviewResponse create(Long expertId, Long subItemId, ReviewCreateRequest reviewCreateRequest) {
		Expert expert = expertRepository.findById(expertId)
				.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_EXPERT));
		Member writer = memberRepository.findById(reviewCreateRequest.writerId())
				.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));
		SubItem subItem = subItemRepository.findById(subItemId)
				.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_SUB_ITEM));

		Review review = ReviewCreateRequest.toReview(reviewCreateRequest, expert, writer, subItem);
		reviewRepository.save(review);

		return ReviewResponse.from(review);
	}

	@Transactional(readOnly = true)
	public ReviewsResponse findAll(Pageable pageable) {
		List<Review> reviews = reviewRepository.findAll(pageable)
				.toList();

		return ReviewsResponse.from(reviews);
	}

	@Transactional(readOnly = true)
	public ReviewsResponse findByExpertId(Pageable pageable, Long expertId) {
		List<Review> reviews = reviewRepository.findAllByExpertId(expertId, pageable);

		if (reviews.isEmpty()) {
			throw new EntityNotFoundException(ErrorCode.NOT_FOUND_REVIEW);
		}

		return ReviewsResponse.from(reviews);
	}

	@Transactional(readOnly = true)
	public ReviewResponse findById(Long id, Pageable pageable) {
		Review review = reviewRepository.findById(id, pageable)
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
		reviewRepository.findById(id).ifPresentOrElse(
				review -> reviewRepository.deleteById(id),
				() -> {
					throw new EntityNotFoundException(ErrorCode.NOT_FOUND_REVIEW);
				}
		);
	}

}
