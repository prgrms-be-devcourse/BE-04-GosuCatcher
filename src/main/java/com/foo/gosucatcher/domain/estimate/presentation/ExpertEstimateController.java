package com.foo.gosucatcher.domain.estimate.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foo.gosucatcher.domain.chat.application.dto.response.MessageResponse;
import com.foo.gosucatcher.domain.estimate.application.ExpertEstimateService;
import com.foo.gosucatcher.domain.estimate.application.dto.request.ExpertAutoEstimateCreateRequest;
import com.foo.gosucatcher.domain.estimate.application.dto.request.ExpertNormalEstimateCreateRequest;
import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertAutoEstimateResponse;
import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertAutoEstimatesResponse;
import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertEstimateResponse;
import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertEstimatesResponse;
import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertNormalEstimateResponse;
import com.foo.gosucatcher.domain.matching.application.MatchingService;
import com.foo.gosucatcher.global.aop.CurrentExpertId;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/expert-estimates")
@RequiredArgsConstructor
public class ExpertEstimateController {

	private final ExpertEstimateService expertEstimateService;
	private final MatchingService matchingService;

	@PostMapping("/normal/{expertId}")
	public ResponseEntity<MessageResponse> createNormal(@PathVariable Long expertId, @RequestParam Long memberEstimateId,
														@Validated @RequestBody ExpertNormalEstimateCreateRequest request) {
		ExpertNormalEstimateResponse expertNormalEstimateResponse = expertEstimateService.createNormal(expertId, memberEstimateId, request);

		MessageResponse messageResponse = matchingService.sendFirstMessage(memberEstimateId, expertNormalEstimateResponse);

		return ResponseEntity.ok(messageResponse);
	}

	@PostMapping("/auto/{expertId}")
	public ResponseEntity<ExpertAutoEstimateResponse> createAuto(@PathVariable Long expertId,
																 @Validated @RequestBody ExpertAutoEstimateCreateRequest request) {
		ExpertAutoEstimateResponse expertAutoEstimateResponse = expertEstimateService.createAuto(expertId, request);

		return ResponseEntity.ok(expertAutoEstimateResponse);
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

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		expertEstimateService.delete(id);

		return ResponseEntity.ok(null);
	}

	@GetMapping("/member-estimates/{memberEstimateId}")
	public ResponseEntity<ExpertEstimatesResponse> findAllByMemberEstimateId(@PathVariable Long memberEstimateId) {
		ExpertEstimatesResponse expertEstimatesResponse = expertEstimateService.findAllByMemberEstimateId(memberEstimateId);

		return ResponseEntity.ok(expertEstimatesResponse);
	}

	@GetMapping("/auto")
	@CurrentExpertId
	public ResponseEntity<ExpertAutoEstimatesResponse> findAllUnmatchedAutoByExpertId(Long expertId) {
		ExpertAutoEstimatesResponse expertAutoEstimatesResponse = expertEstimateService.findAllUnmatchedAutoByExpertId(expertId);

		return ResponseEntity.ok(expertAutoEstimatesResponse);
	}
}
