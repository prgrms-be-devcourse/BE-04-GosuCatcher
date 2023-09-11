package com.foo.gosucatcher.domain.review.presentation;

import java.net.URI;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.foo.gosucatcher.domain.review.application.ReviewService;
import com.foo.gosucatcher.domain.review.application.dto.request.ReplyRequest;
import com.foo.gosucatcher.domain.review.application.dto.request.ReviewCreateRequest;
import com.foo.gosucatcher.domain.review.application.dto.request.ReviewUpdateRequest;
import com.foo.gosucatcher.domain.review.application.dto.response.ReplyResponse;
import com.foo.gosucatcher.domain.review.application.dto.response.ReviewResponse;
import com.foo.gosucatcher.domain.review.application.dto.response.ReviewsResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewController {

	private final ReviewService reviewService;
	private final int DEFAULT_PAGING_SIZE = 3;

	@PostMapping("/{expertId}")
	public ResponseEntity<ReviewResponse> create(
		@PathVariable Long expertId,
		@RequestParam Long subItemId,
		@Validated @RequestBody ReviewCreateRequest reviewCreateRequest) {
		ReviewResponse reviewResponse = reviewService.create(expertId, subItemId, reviewCreateRequest);

		return ResponseEntity.ok(reviewResponse);
	}

	@GetMapping
	public ResponseEntity<ReviewsResponse> findAll(
		@PageableDefault(sort = "updatedAt", size = DEFAULT_PAGING_SIZE, direction = Sort.Direction.DESC)
		Pageable pageable) {
		ReviewsResponse response = reviewService.findAll(pageable);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/experts/{expertId}")
	public ResponseEntity<ReviewsResponse> findAllByExpertId(@PathVariable Long expertId,
		@RequestParam(required = false) Long subItemId,
		@PageableDefault(sort = "updatedAt", size = DEFAULT_PAGING_SIZE, direction = Sort.Direction.DESC)
		Pageable pageable) {

		ReviewsResponse response = reviewService.findAllByExpertIdAndSubItem(pageable, expertId, subItemId);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/experts/{expertId}/counts") //평균 평점 반환 API 추가
	public ResponseEntity<Long> countByExpertId(@PathVariable Long expertId) {
		long count = reviewService.countByExpertId(expertId);

		return ResponseEntity.ok(count);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ReviewResponse> findById(@PathVariable Long id) {
		ReviewResponse reviewResponse = reviewService.findById(id);

		return ResponseEntity.ok(reviewResponse);
	}

	@PatchMapping("/{id}")
	//TODO : security 적용하여 수정자 == 원글 작성자 비교
	public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody ReviewUpdateRequest reviewUpdateRequest) {
		Long updatedId = reviewService.update(id, reviewUpdateRequest);

		return ResponseEntity.ok(updatedId);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Object> delete(@PathVariable Long id) {
		reviewService.delete(id);

		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{reviewId}/replies")
	public ResponseEntity<ReplyResponse> createReply(
		@PathVariable Long reviewId,
		@Validated @RequestBody ReplyRequest replyRequest) {
		ReplyResponse replyResponse = reviewService.createReply(reviewId, replyRequest);

		URI uri = ServletUriComponentsBuilder
			.fromCurrentRequest()
			.path("/{id}")
			.buildAndExpand(replyResponse.id())
			.toUri();

		return ResponseEntity.created(uri)
			.body(replyResponse);
	}


	@PatchMapping("/{reviewId}/replies/{replyId}")
	public ResponseEntity<Long> updateReply(
		@PathVariable Long reviewId,
		@PathVariable Long replyId,
		@Validated @RequestBody ReplyRequest replyRequest) {

		long updatedId = reviewService.updateReply(reviewId, replyId, replyRequest);

		return ResponseEntity.ok(updatedId);
	}

	@GetMapping("/{reviewId}/replies/{replyId}")
	public ResponseEntity<ReplyResponse> findReplyByID(@PathVariable Long reviewId, @PathVariable Long replyId) {
		ReplyResponse replyResponse = reviewService.findReplyById(reviewId, replyId);

		return ResponseEntity.ok(replyResponse);
	}

	@DeleteMapping("/{reviewId}/replies/{replyId}")
	public ResponseEntity<Object> deleteReply(@PathVariable Long reviewId, @PathVariable Long replyId) {
		reviewService.deleteReply(replyId);

		return ResponseEntity.noContent().build();
	}
}
