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

import com.foo.gosucatcher.domain.estimate.application.ExpertEstimateService;
import com.foo.gosucatcher.domain.estimate.application.dto.request.ExpertEstimateCreateRequest;
import com.foo.gosucatcher.domain.estimate.application.dto.request.ExpertEstimateUpdateRequest;
import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertEstimateResponse;
import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertEstimatesResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/expert-response-estimates")
@RequiredArgsConstructor
public class ExpertEstimateController {

	private final ExpertEstimateService expertEstimateService;

	@PostMapping("/{expertId}")
	public ResponseEntity<ExpertEstimateResponse> create(@PathVariable Long expertId,
														 @Validated @RequestBody ExpertEstimateCreateRequest request) {
		ExpertEstimateResponse expertEstimateResponse = expertEstimateService.create(expertId,
			request);

		return ResponseEntity.ok(expertEstimateResponse);
	}

	@GetMapping
	public ResponseEntity<ExpertEstimatesResponse> findAll() {
		ExpertEstimatesResponse estimatesResponse = expertEstimateService.findAll();

		return ResponseEntity.ok(estimatesResponse);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ExpertEstimateResponse> findOne(@PathVariable Long id) {
		ExpertEstimateResponse estimateResponse = expertEstimateService.findById(id);

		return ResponseEntity.ok(estimateResponse);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<Long> update(@PathVariable Long id,
									   @Validated @RequestBody ExpertEstimateUpdateRequest request) {
		Long updatedId = expertEstimateService.update(id, request);

		return ResponseEntity.ok(updatedId);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		expertEstimateService.delete(id);

		return ResponseEntity.ok(null);
	}
}
