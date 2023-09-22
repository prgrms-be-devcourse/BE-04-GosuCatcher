package com.foo.gosucatcher.domain.review.presentation;

import java.net.URI;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.foo.gosucatcher.domain.image.application.dto.request.ImageUploadRequest;
import com.foo.gosucatcher.domain.review.application.ReviewService;
import com.foo.gosucatcher.domain.review.application.dto.request.ReplyRequest;
import com.foo.gosucatcher.domain.review.application.dto.request.ReviewCreateRequest;
import com.foo.gosucatcher.domain.review.application.dto.request.ReviewUpdateRequest;
import com.foo.gosucatcher.domain.review.application.dto.response.ReplyResponse;
import com.foo.gosucatcher.domain.review.application.dto.response.ReviewResponse;
import com.foo.gosucatcher.domain.review.application.dto.response.ReviewsResponse;
import com.foo.gosucatcher.global.aop.CurrentMemberId;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewController {

	private static final int DEFAULT_PAGING_SIZE = 3;

	private final ReviewService reviewService;

	@PostMapping("/{expertId}")
	@CurrentMemberId
	@Operation(summary = "리뷰 생성 요청", description = "리뷰가 추가됩니다.", tags = {"Review Controller"})
	public ResponseEntity<ReviewResponse> create(
		@Parameter(description = "서비스 제공 고수 ID", required = true, example = "1")
		@PathVariable Long expertId,

		@Parameter(description = "세부서비스 ID", example = "1")
		@RequestParam Long subItemId,

		@Parameter(description = "리뷰 요청 정보", required = true)
		@Validated @RequestPart ReviewCreateRequest reviewCreateRequest,

		@Parameter(description = "리뷰에 첨부한 이미지")
		@RequestPart(required = false) List<MultipartFile> imageFiles,

		@Parameter(description = "작성자 ID", required = true, example = "1")
		Long memberId
	) {
		ImageUploadRequest imageUploadRequest = new ImageUploadRequest(imageFiles);

		ReviewResponse response = reviewService.create(expertId, subItemId, memberId, reviewCreateRequest,
			imageUploadRequest);

		URI uri = ServletUriComponentsBuilder
			.fromCurrentRequest()
			.path("/{expertId}")
			.buildAndExpand(response.id())
			.toUri();

		return ResponseEntity.created(uri)
			.body(response);
	}

	@GetMapping
	public ResponseEntity<ReviewsResponse> findAll(
		@PageableDefault(sort = "updatedAt", size = DEFAULT_PAGING_SIZE, direction = Sort.Direction.DESC)
		Pageable pageable
	) {
		ReviewsResponse response = reviewService.findAll(pageable);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/experts")
	public ResponseEntity<ReviewsResponse> findAllByExpertId(
		@RequestParam Long id,
		@RequestParam(required = false) Long subItemId,
		@PageableDefault(sort = "updatedAt", size = DEFAULT_PAGING_SIZE, direction = Sort.Direction.DESC)
		Pageable pageable
	) {
		ReviewsResponse response = reviewService.findAllByExpertIdAndSubItem(id, subItemId, pageable);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/counts/experts")
	public ResponseEntity<Long> countByExpertId(@RequestParam Long id) {
		long count = reviewService.countByExpertId(id);

		return ResponseEntity.ok(count);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ReviewResponse> findById(@PathVariable Long id) {
		ReviewResponse reviewResponse = reviewService.findById(id);

		return ResponseEntity.ok(reviewResponse);
	}

	@PatchMapping("/{id}")
	@CurrentMemberId
	public ResponseEntity<Long> update(
		@PathVariable Long id,
		@RequestBody ReviewUpdateRequest reviewUpdateRequest,
		Long memberId
	) {
		long updatedId = reviewService.update(id, memberId, reviewUpdateRequest);

		return ResponseEntity.ok(updatedId);
	}

	@DeleteMapping("/{id}")
	@CurrentMemberId
	public ResponseEntity<Object> delete(@PathVariable Long id, Long memberId) {
		reviewService.delete(id, memberId);

		return ResponseEntity.noContent()
			.build();
	}

	@PostMapping("/{reviewId}/replies")
	@CurrentMemberId
	public ResponseEntity<ReplyResponse> createReply(
		@PathVariable Long reviewId,
		@Validated @RequestBody ReplyRequest replyRequest,
		Long memberId
	) {
		ReplyResponse response = reviewService.createReply(reviewId, memberId, replyRequest);

		URI uri = ServletUriComponentsBuilder
			.fromCurrentRequest()
			.path("/{reviewId}/replies")
			.buildAndExpand(response.id())
			.toUri();

		return ResponseEntity.created(uri)
			.body(response);
	}

	@PatchMapping("/replies")
	@CurrentMemberId
	public ResponseEntity<Long> updateReply(
		@RequestParam Long id,
		@Validated @RequestBody ReplyRequest replyRequest,
		Long memberId
	) {
		long updatedId = reviewService.updateReply(id, replyRequest, memberId);

		return ResponseEntity.ok(updatedId);
	}

	@DeleteMapping("/replies")
	@CurrentMemberId
	public ResponseEntity<Object> deleteReply(
		@RequestParam Long id,
		Long memberId
	) {
		reviewService.deleteReply(id, memberId);

		return ResponseEntity.noContent()
			.build();
	}
}
