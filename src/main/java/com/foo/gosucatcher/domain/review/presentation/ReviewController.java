package com.foo.gosucatcher.domain.review.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foo.gosucatcher.domain.review.application.ReviewService;
import com.foo.gosucatcher.domain.review.application.dto.request.ReviewCreateRequest;
import com.foo.gosucatcher.domain.review.application.dto.request.ReviewUpdateRequest;
import com.foo.gosucatcher.domain.review.application.dto.response.ReviewResponse;
import com.foo.gosucatcher.domain.review.application.dto.response.ReviewResponses;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewController {

	private final ReviewService reviewService;

	@PostMapping
	public ResponseEntity<ReviewResponse> create(@Validated @RequestBody ReviewCreateRequest reviewCreateRequest) {
		ReviewResponse reviewResponse = reviewService.create(reviewCreateRequest);

		return ResponseEntity.ok(reviewResponse);
	}

	@GetMapping
	public ResponseEntity<ReviewResponses> findAll() {
		ReviewResponses reviewResponses = reviewService.findAll();

		return ResponseEntity.ok(reviewResponses);
	}

	@GetMapping("/experts/{expertId}")
	public ResponseEntity<ReviewResponses> findByExpertId(@PathVariable Long expertId) {
		ReviewResponses reviewResponses = reviewService.findByExpertId(expertId);

		return ResponseEntity.ok(reviewResponses);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ReviewResponse> findById(@PathVariable Long id) {
		ReviewResponse reviewResponse = reviewService.findById(id);

		return ResponseEntity.ok(reviewResponse);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody ReviewUpdateRequest reviewUpdateRequest) {
		Long updatedId = reviewService.update(id, reviewUpdateRequest);

		return ResponseEntity.ok(updatedId);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		reviewService.delete(id);

		return ResponseEntity.ok(null);
	}
}
