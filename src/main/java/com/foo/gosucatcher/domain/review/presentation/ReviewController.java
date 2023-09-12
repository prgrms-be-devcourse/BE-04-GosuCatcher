package com.foo.gosucatcher.domain.review.presentation;

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

import com.foo.gosucatcher.domain.review.application.ReviewService;
import com.foo.gosucatcher.domain.review.application.dto.request.ReviewCreateRequest;
import com.foo.gosucatcher.domain.review.application.dto.request.ReviewUpdateRequest;
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

		URI uri = ServletUriComponentsBuilder
			.fromCurrentRequest()
			.path("/{id}")
			.buildAndExpand(reviewResponse.id())
			.toUri();

		return ResponseEntity.created(uri)
			.body(reviewResponse);
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

	@GetMapping("/experts/{expertId}/counts")
	public ResponseEntity<Long> countByExpertId(@PathVariable Long expertId) {
		long count = reviewService.countByExpertId(expertId);

		return ResponseEntity.ok(count);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ReviewResponse> findById(@PathVariable Long id) {
		ReviewResponse reviewResponse = reviewService.findById(id);

		return ResponseEntity.ok(reviewResponse);
	}

	@GetMapping("/parents/{parentId}")
	public ResponseEntity<ReviewsResponse> findByParentId(@PathVariable Long parentId, Pageable pageable) {
		ReviewsResponse reviewsResponse = reviewService.findByParentId(parentId, pageable);

		return ResponseEntity.ok(reviewsResponse);
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

		return ResponseEntity.noContent()
			.build();
	}
}
