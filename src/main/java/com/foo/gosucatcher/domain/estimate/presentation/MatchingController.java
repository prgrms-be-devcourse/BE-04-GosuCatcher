package com.foo.gosucatcher.domain.estimate.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foo.gosucatcher.domain.estimate.application.MatchingService;
import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertEstimatesResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/matching")
@RequiredArgsConstructor
public class MatchingController {

	private final MatchingService matchingService;

	@PostMapping("/{subItemId}/{location}")
	public ResponseEntity<ExpertEstimatesResponse> match(@PathVariable Long subItemId, @PathVariable String location) {
		ExpertEstimatesResponse expertEstimatesResponse = matchingService.match(subItemId, location);

		return ResponseEntity.ok(expertEstimatesResponse);
	}
}
