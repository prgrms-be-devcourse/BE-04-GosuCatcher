package com.foo.gosucatcher.domain.review.presentation;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foo.gosucatcher.domain.review.application.ReviewService;
import com.foo.gosucatcher.domain.review.application.dto.request.ReviewRequest;
import com.foo.gosucatcher.domain.review.application.dto.response.ReviewResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewController {

	private final ReviewService reviewService;

	@PostMapping
	public ResponseEntity<ReviewResponse> create(@Valid @RequestBody ReviewRequest reviewRequest) {
		ReviewResponse reviewResponse = reviewService.create(reviewRequest);

		return ResponseEntity.ok(reviewResponse);
	}

	@GetMapping("/{expertId}")
	public ResponseEntity<ReviewResponse> findByExpertId(@PathVariable Long expertId) {
		ReviewResponse reviewResponse = reviewService.findByExpertId(expertId);

		return ResponseEntity.ok(reviewResponse);
	}
}
