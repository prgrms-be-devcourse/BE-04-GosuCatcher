package com.foo.gosucatcher.domain.estimate.presentation;

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

import com.foo.gosucatcher.domain.estimate.application.ExpertResponseEstimateService;
import com.foo.gosucatcher.domain.estimate.application.dto.request.ExpertResponseEstimateCreateRequest;
import com.foo.gosucatcher.domain.estimate.application.dto.request.ExpertResponseEstimateUpdateRequest;
import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertResponseEstimateResponse;
import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertResponseEstimatesResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/expert-response-estimates")
@RequiredArgsConstructor
public class ExpertResponseEstimateController {

	private final ExpertResponseEstimateService expertResponseEstimateService;

	@PostMapping("/{expertId}")
	public ResponseEntity<ExpertResponseEstimateResponse> create(@PathVariable Long expertId,
		@Validated @RequestBody ExpertResponseEstimateCreateRequest request) {

		ExpertResponseEstimateResponse expertResponseEstimateResponse = expertResponseEstimateService.create(expertId,
			request);

		return ResponseEntity.ok(expertResponseEstimateResponse);
	}

	@GetMapping
	public ResponseEntity<ExpertResponseEstimatesResponse> findAll() {
		ExpertResponseEstimatesResponse estimatesResponse = expertResponseEstimateService.findAll();

		return ResponseEntity.ok(estimatesResponse);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ExpertResponseEstimateResponse> findOne(@PathVariable Long id) {
		ExpertResponseEstimateResponse estimateResponse = expertResponseEstimateService.findById(id);

		return ResponseEntity.ok(estimateResponse);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<Long> update(@PathVariable Long id,
		@Validated @RequestBody ExpertResponseEstimateUpdateRequest request) {

		Long updatedId = expertResponseEstimateService.update(id, request);

		return ResponseEntity.ok(updatedId);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		expertResponseEstimateService.delete(id);

		return ResponseEntity.ok(null);
	}
}
