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
	@Operation(summary = "리뷰 생성 요청", description = "리뷰가 추가됩니다.", tags = {"ReviewController"})
	public ResponseEntity<ReviewResponse> create(
		@Parameter(description = "서비스 제공 고수 ID", required = true)
		@PathVariable Long expertId,

		@Parameter(description = "세부서비스 ID")
		@RequestParam Long subItemId,

		@Parameter(description = "리뷰 요청 정보", required = true)
		@Validated @RequestPart ReviewCreateRequest reviewCreateRequest,

		@Parameter(description = "리뷰에 첨부한 이미지")
		@RequestPart(required = false) List<MultipartFile> imageFiles,

		@Parameter(description = "작성자 ID", required = true)
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
	@Operation(summary = "리뷰/답글 전체 조회", description = "현재까지 작성된 모든 리뷰/답글을 조회합니다.", tags = {"ReviewController"})
	public ResponseEntity<ReviewsResponse> findAll(
		@Parameter(description = "조회 할 데이터 범위, 개수, 정렬 기준")
		@PageableDefault(sort = "updatedAt", size = DEFAULT_PAGING_SIZE, direction = Sort.Direction.DESC)
		Pageable pageable
	) {
		ReviewsResponse response = reviewService.findAll(pageable);

		return ResponseEntity.ok(response);
	}

	@Operation(summary = "특정 고수에 대한 리뷰/답글 조회", description = "특정 고수에 대해 작성된 리뷰를 조회합니다(고수가 제공하는 서비스별로 조회 가능합니다)", tags = {
		"ReviewController"})
	@GetMapping("/experts")
	public ResponseEntity<ReviewsResponse> findAllByExpertId(
		@Parameter(description = "고수 ID", required = true)
		@RequestParam Long id,

		@Parameter(description = "세부 서비스 ID")
		@RequestParam(required = false) Long subItemId,

		@Parameter(description = "조회 할 데이터 범위, 개수, 정렬 기준")
		@PageableDefault(sort = "updatedAt", size = DEFAULT_PAGING_SIZE, direction = Sort.Direction.DESC)
		Pageable pageable
	) {
		ReviewsResponse response = reviewService.findAllByExpertIdAndSubItem(id, subItemId, pageable);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/counts/experts")
	@Operation(summary = "특정 고수에 대한 리뷰/답글 개수 조회", description = "특정 고수에 대해 작성된 리뷰 및 답글의 개수를 조회합니다", tags = {
		"ReviewController"})
	public ResponseEntity<Long> countByExpertId(
		@Parameter(description = "조회 대상이 되는 고수 ID")
		@RequestParam Long id
	) {
		long count = reviewService.countByExpertId(id);

		return ResponseEntity.ok(count);
	}

	@GetMapping("/{id}")
	@Operation(summary = "아이디에 해당하는 리뷰와 그에 대한 답글 조회", description = "리뷰 아이디에 해당하는 리뷰와 그에 대한 답글이 있는 경우 답글까지 조회합니다", tags = {
		"ReviewController"})
	public ResponseEntity<ReviewResponse> findById(
		@Parameter(description = "리뷰 ID")
		@PathVariable Long id
	) {
		ReviewResponse reviewResponse = reviewService.findById(id);

		return ResponseEntity.ok(reviewResponse);
	}

	@PatchMapping("/{id}")
	@CurrentMemberId
	@Operation(summary = "리뷰 수정", description = "리뷰 내용 및 별점을 수정합니다")
	public ResponseEntity<Long> update(
		@Parameter(description = "수정하고자 하는 리뷰 ID")
		@PathVariable Long id,

		@Parameter(description = "요청 정보")
		@RequestBody ReviewUpdateRequest reviewUpdateRequest,
		Long memberId
	) {
		long updatedId = reviewService.update(id, memberId, reviewUpdateRequest);

		return ResponseEntity.ok(updatedId);
	}

	@DeleteMapping("/{id}")
	@CurrentMemberId
	@Operation(summary = "리뷰 삭제", description = "특정 리뷰를 삭제한다")
	public ResponseEntity<Object> delete(
		@Parameter(description = "삭제하고자 하는 리뷰ID")
		@PathVariable Long id,

		@Parameter(description = "수정 요청하는 사용자의 ID")
		Long memberId
	) {
		reviewService.delete(id, memberId);

		return ResponseEntity.noContent()
			.build();
	}

	@PostMapping("/{reviewId}/replies")
	@CurrentMemberId
	@Operation(summary = "답글 추가", description = "리뷰에 대한 답글을 작성한다")
	public ResponseEntity<ReplyResponse> createReply(
		@Parameter(description = "답글을 추가하고자 하는 상위 리뷰 ID")
		@PathVariable Long reviewId,
		@Validated @RequestBody ReplyRequest replyRequest,

		@Parameter(description = "작성자 ID")
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
	@Operation(summary = "답글 수정", description = "답글 본문을 수정한다")
	public ResponseEntity<Long> updateReply(
		@Parameter(description = "답글 ID")
		@RequestParam Long id,

		@Validated @RequestBody ReplyRequest replyRequest,
		Long memberId
	) {
		long updatedId = reviewService.updateReply(id, replyRequest, memberId);

		return ResponseEntity.ok(updatedId);
	}

	@Operation(summary = "답글 삭제")
	@DeleteMapping("/replies")
	@CurrentMemberId
	public ResponseEntity<Object> deleteReply(
		@Parameter(description = "삭제하고자하는 답글 ID")
		@RequestParam Long id,
		Long memberId
	) {
		reviewService.deleteReply(id, memberId);

		return ResponseEntity.noContent()
			.build();
	}
}
