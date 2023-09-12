package com.foo.gosucatcher.domain.review.application;

import static com.foo.gosucatcher.global.error.ErrorCode.NOT_FOUND_EXPERT;
import static com.foo.gosucatcher.global.error.ErrorCode.NOT_FOUND_REVIEW;
import static com.foo.gosucatcher.global.error.ErrorCode.UNSUPPORTED_MULTIPLE_REPLIES;
import static com.foo.gosucatcher.global.error.ErrorCode.UNSUPPORTED_REPLIER;

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
import com.foo.gosucatcher.domain.review.application.dto.request.ReplyRequest;
import com.foo.gosucatcher.domain.review.application.dto.request.ReviewCreateRequest;
import com.foo.gosucatcher.domain.review.application.dto.request.ReviewUpdateRequest;
import com.foo.gosucatcher.domain.review.application.dto.response.ReplyResponse;
import com.foo.gosucatcher.domain.review.application.dto.response.ReviewResponse;
import com.foo.gosucatcher.domain.review.application.dto.response.ReviewsResponse;
import com.foo.gosucatcher.domain.review.domain.Reply;
import com.foo.gosucatcher.domain.review.domain.ReplyRepository;
import com.foo.gosucatcher.domain.review.domain.Review;
import com.foo.gosucatcher.domain.review.domain.ReviewRepository;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;
import com.foo.gosucatcher.global.error.exception.InvalidReplyCountException;
import com.foo.gosucatcher.global.error.exception.UnsupportedReplierException;

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

		Review review = ReviewCreateRequest.toReview(reviewCreateRequest, expert, writer, subItem);
		updateExpert(expert, reviewCreateRequest.rating(), false);
		reviewRepository.save(review);

		return ReviewResponse.from(review);
	}

	@Transactional(readOnly = true)
	public ReviewsResponse findAll(Pageable pageable) {
		Page<Review> reviews = reviewRepository.findAllByOrderByCreatedAt(pageable);

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
		updateExpert(review.getExpert(), reviewUpdateRequest.rating(), false);

		return id;
	}

	public void delete(Long id) {
		reviewRepository.findById(id).ifPresentOrElse(
			review -> {
				reviewRepository.deleteById(id);
				if(review.replyExists()){
					deleteReply(id, review.getReply().getId());
				}
				updateExpert(review.getExpert(), review.getRating(), true);
			},
			() -> {
				throw new EntityNotFoundException(NOT_FOUND_REVIEW);
			}
		);
	}

	@Transactional(readOnly = true)
	public long countByExpertId(Long expertId) {
		expertRepository.findById(expertId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_EXPERT));

		long count = reviewRepository.countByExpertId(expertId);

		return count;
	}

	public ReplyResponse createReply(Long reviewId, ReplyRequest replyRequest) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_REVIEW));

		if(review.replyExists()){
			throw new InvalidReplyCountException(UNSUPPORTED_MULTIPLE_REPLIES);
		}

		validateReplyWriter(review, replyRequest);

		Reply reply = ReplyRequest.toReply(replyRequest);
		replyRepository.save(reply);
		review.updateReply(reply);

		return ReplyResponse.of(review.getId(), reply);
	}

	public long updateReply(Long reviewId, long replyId, ReplyRequest replyRequest) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_REVIEW));

		validateReplyWriter(review, replyRequest);

		Reply reply = replyRepository.findById(replyId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_REVIEW));

		Reply updatedReply = ReplyRequest.toReply(replyRequest);
		reply.update(updatedReply);

		return replyId;
	}

	public ReplyResponse findReplyById(Long reviewId, Long replyId) {
		Reply reply = replyRepository.findById(replyId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_REVIEW));

		return ReplyResponse.of(reviewId, reply);
	}

	public void deleteReply(Long reviewId, Long replyId) {
		reviewRepository.findById(reviewId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_REVIEW));

		replyRepository.findById(replyId).ifPresentOrElse(
			reply -> replyRepository.deleteById(replyId),
			() -> {
				throw new EntityNotFoundException(NOT_FOUND_REVIEW);
			}
		);
	}

	private void validateReplyWriter(Review review, ReplyRequest replyRequest) {
		long expertId = review.getExpert().getId();
		long writerId = replyRequest.writerId();

		if (expertId != writerId) {
			throw new UnsupportedReplierException(UNSUPPORTED_REPLIER);
		}
	}

	private void updateExpert(Expert expert, double rating, boolean isDeletion) {
		int expertReviewCount = expert.getReviewCount();
		double expertRating = expert.getRating();
		if (isDeletion) {
			expertRating *= -1;
			expertReviewCount *= -1;
		}

		double updatedRating = Math.max(0,
			((expertRating * expertReviewCount) + rating) / (expertReviewCount + 1));
		String adjustedRating = String.format("%.1f", updatedRating);

		Expert updatedExpert = Expert.builder()
			.member(expert.getMember())
			.storeName(expert.getStoreName())
			.location(expert.getLocation())
			.maxTravelDistance(expert.getMaxTravelDistance())
			.description(expert.getDescription())
			.rating(Double.parseDouble(adjustedRating))
			.reviewCount(Math.max(0, expertReviewCount + 1))
			.build();

		expert.update(updatedExpert);
	}

}
