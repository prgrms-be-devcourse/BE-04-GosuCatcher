package com.foo.gosucatcher.domain.review.application;

import static com.foo.gosucatcher.global.error.ErrorCode.EXCESSIVE_IMAGE_COUNT;
import static com.foo.gosucatcher.global.error.ErrorCode.NOT_FOUND_EXPERT;
import static com.foo.gosucatcher.global.error.ErrorCode.NOT_FOUND_REVIEW;
import static com.foo.gosucatcher.global.error.ErrorCode.UNSUPPORTED_MULTIPLE_REPLIES;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.expert.domain.ExpertRepository;
import com.foo.gosucatcher.domain.image.ImageService;
import com.foo.gosucatcher.domain.image.application.dto.request.ImageUploadRequest;
import com.foo.gosucatcher.domain.image.application.dto.response.ImagesResponse;
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
import com.foo.gosucatcher.domain.review.domain.ReviewImage;
import com.foo.gosucatcher.domain.review.domain.ReviewImageRepository;
import com.foo.gosucatcher.domain.review.domain.ReviewRepository;
import com.foo.gosucatcher.domain.review.exception.InvalidImageFileCountException;
import com.foo.gosucatcher.domain.review.exception.InvalidReplyCountException;
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
	private final ReviewImageRepository reviewImageRepository;
	private final ImageService imageService;

	private static final int IMAGE_MAX_COUNT = 5;

	public ReviewResponse create(Long expertId, Long subItemId, Long writerId,
		ReviewCreateRequest reviewCreateRequest, ImageUploadRequest imageUploadRequest) {
		Expert expert = expertRepository.findById(expertId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_EXPERT));
		Member writer = memberRepository.findById(writerId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));
		SubItem subItem = subItemRepository.findById(subItemId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_SUB_ITEM));

		expert.addRating(reviewCreateRequest.rating());

		Review review = ReviewCreateRequest.toReview(reviewCreateRequest, expert, writer, subItem);
		reviewRepository.save(review);

		saveImages(review, imageUploadRequest);

		return ReviewResponse.from(review);
	}

	private void saveImages(Review review, ImageUploadRequest imageUploadRequest) {
		if (imageUploadRequest.files() == null) {
			return;
		}

		if (imageUploadRequest.files().size() > IMAGE_MAX_COUNT) {
			throw new InvalidImageFileCountException(EXCESSIVE_IMAGE_COUNT);
		}

		ImagesResponse imagesResponse = imageService.store(imageUploadRequest);

		for (String filename : imagesResponse.filenames()) {
			ReviewImage reviewImage = ReviewImage.of(review, filename);
			reviewImageRepository.save(reviewImage);
		}

		List<ReviewImage> reviewImages = ImagesResponse.toReviewImages(review, imagesResponse);
		review.addReviewImages(reviewImages);
	}

	@Transactional(readOnly = true)
	public ReviewsResponse findAll(Pageable pageable) {
		Page<Review> reviews = reviewRepository.findAllByOrderByCreatedAt(pageable);

		return ReviewsResponse.from(reviews);
	}

	@Transactional(readOnly = true)
	public ReviewsResponse findAllByExpertIdAndSubItem(Long expertId, Long subItemId, Pageable pageable) {
		Slice<Review> reviews;
		reviews = reviewRepository.findAllByExpertIdAndSubItemIdOrderByCreatedAt(expertId, subItemId, pageable);

		return ReviewsResponse.from(reviews);
	}

	@Transactional(readOnly = true)
	public ReviewResponse findById(Long id) {
		Review review = reviewRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_REVIEW));

		return ReviewResponse.from(review);
	}

	public Long update(Long id, Long updaterId, ReviewUpdateRequest reviewUpdateRequest) {
		Review review = reviewRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_REVIEW));

		Review updatedReview = ReviewUpdateRequest.toReview(reviewUpdateRequest);
		review.update(updatedReview, updaterId);

		return id;
	}

	public void delete(Long id, Long updaterId) {
		Review review = reviewRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_REVIEW));

		review.delete(updaterId);
		reviewRepository.deleteById(id);
	}

	@Transactional(readOnly = true)
	public long countByExpertId(Long expertId) {
		Expert expert = expertRepository.findById(expertId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_EXPERT));

		return reviewRepository.countByExpertId(expert.getId());
	}

	public ReplyResponse createReply(long reviewId, long writerId, ReplyRequest replyRequest) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_REVIEW));

		Reply reply = ReplyRequest.toReply(replyRequest, review);
		reply.validateWriter(writerId, review);

		if (review.replyExists()) {
			throw new InvalidReplyCountException(UNSUPPORTED_MULTIPLE_REPLIES);
		}

		replyRepository.save(reply);

		return ReplyResponse.of(review.getId(), reply);
	}

	public long updateReply(long replyId, ReplyRequest replyRequest, Long updaterId) {
		Reply reply = replyRepository.findById(replyId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_REVIEW));

		Reply updatedReply = ReplyRequest.toReply(replyRequest);

		reply.update(updatedReply, updaterId);

		return replyId;
	}

	public void deleteReply(Long id, Long updaterId) {
		Reply reply = replyRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_REVIEW));

		reply.validateWriter(updaterId);

		replyRepository.deleteById(id);
	}
}
