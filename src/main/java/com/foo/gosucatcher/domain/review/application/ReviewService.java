package com.foo.gosucatcher.domain.review.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
import com.foo.gosucatcher.domain.review.domain.ReplyRepository;
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
	private final ReplyRepository replyRepository;
	private final ExpertRepository expertRepository;
	private final MemberRepository memberRepository;
	private final SubItemRepository subItemRepository;

	public ReviewResponse create(Long expertId, Long subItemId, ReviewCreateRequest reviewCreateRequest) {
		Expert expert = expertRepository.findById(expertId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_EXPERT));
		Member writer = memberRepository.findById(reviewCreateRequest.writerId())
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));
		SubItem subItem = subItemRepository.findById(subItemId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_SUB_ITEM));

		Review parent = null;

		Review review = ReviewCreateRequest.toReview(reviewCreateRequest, expert, writer, subItem, parent);

		boolean isReply = reviewCreateRequest.parentId() != null;
		if (isReply) {
			parent = reviewRepository.findById(reviewCreateRequest.parentId())
				.orElseThrow(() -> new EntityNotFoundException(ErrorCode.UNSUPPORTED_REPLY));
			review.updateParent(parent);
		}

		reviewRepository.save(review);
		return ReviewResponse.from(review);
	}

	@Transactional(readOnly = true)
	public ReviewsResponse findAll(Pageable pageable) {
		Page<Review> reviews = reviewRepository.findAllOrderByCreatedAt(pageable);

		return ReviewsResponse.from(reviews);
	}

	@Transactional(readOnly = true)
	public ReviewsResponse findAllByExpertIdAndSubItem(Pageable pageable, Long expertId, Long subItemId) {
		Slice<Review> reviews;

		if (subItemId == null) {
			reviews = reviewRepository.findAllByExpertIdOrderByCreatedAt(expertId, pageable);
		} else {
			reviews = reviewRepository.findAllByExpertIdAndSubItemIdOrderByCreatedAt(expertId, subItemId, pageable);
		}

		return ReviewsResponse.from(reviews);

	}

	@Transactional(readOnly = true)
	public ReviewResponse findById(Long id) {
		Review review = reviewRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_REVIEW));

		return ReviewResponse.from(review);
	}

	public Long update(Long id, ReviewUpdateRequest reviewUpdateRequest) {
		Review review = reviewRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_REVIEW));

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

	@Transactional(readOnly = true)
	public long countByExpertId(Long expertId) {
		expertRepository.findById(expertId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_EXPERT));

		long count = reviewRepository.countByExpertId(expertId);

		return count;
	}

	public ReviewsResponse findByParentId(Long parentId, Pageable pageable) {
		Slice<Review> reviews = reviewRepository.findByParentIdOrderByCreatedAt(parentId, pageable);

		return ReviewsResponse.from(reviews);
	}
}
