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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "ExpertEstimateController", description = "일반 견적 응답서, 바로 견적서 조회/생성/삭제 API")
@RestController
@RequestMapping("/api/v1/expert-estimates")
@RequiredArgsConstructor
public class ExpertEstimateController {

	private final ExpertEstimateService expertEstimateService;
	private final MatchingService matchingService;

	@Operation(summary = "고수 일반 견적 응답서 생성", description = "고수 일반 견적 응답서를 생성합니다.", tags = {"ExpertEstimateController"})
	@PostMapping("/normal")
	@CurrentExpertId
	public ResponseEntity<MessageResponse> createNormal(
		@Parameter(description = "고수 ID", required = true)
		Long expertId,

		@Parameter(description = "회원 견적서 ID", required = true)
		@RequestParam Long memberEstimateId,

		@Parameter(description = "고수 일반 견적 응답서 생성 정보", required = true)
		@Validated @RequestBody ExpertNormalEstimateCreateRequest request) {
		ExpertNormalEstimateResponse expertNormalEstimateResponse = expertEstimateService.createNormal(expertId, memberEstimateId, request);

		MessageResponse messageResponse = matchingService.sendFirstMessageForNormal(memberEstimateId, expertNormalEstimateResponse);

		return ResponseEntity.ok(messageResponse);
	}

	@Operation(summary = "고수 바로 견적서 생성", description = "고수 바로 견적서를 생성합니다.", tags = {"ExpertEstimateController"})
	@PostMapping("/auto")
	@CurrentExpertId
	public ResponseEntity<ExpertAutoEstimateResponse> createAuto(
		@Parameter(description = "고수 ID", required = true)
		Long expertId,

		@Parameter(description = "고수 바로 견적 응답서 생성 정보", required = true)
		@Validated @RequestBody ExpertAutoEstimateCreateRequest request) {
		ExpertAutoEstimateResponse expertAutoEstimateResponse = expertEstimateService.createAuto(expertId, request);

		return ResponseEntity.ok(expertAutoEstimateResponse);
	}

	@Operation(summary = "모든 고수 견적서 목록 조회", description = "모든 고수 견적서 목록을 조회합니다.", tags = {"ExpertEstimateController"})
	@GetMapping
	public ResponseEntity<ExpertEstimatesResponse> findAll() {
		ExpertEstimatesResponse estimatesResponse = expertEstimateService.findAll();

		return ResponseEntity.ok(estimatesResponse);
	}

	@Operation(summary = "고수 ID로 고수 견적서 조회", description = "고수 ID로 고수 견적서를 조회합니다.", tags = {"ExpertEstimateController"})
	@GetMapping("/{id}")
	public ResponseEntity<ExpertEstimateResponse> findOne(
		@Parameter(description = "고수 견적서 ID", required = true)
		@PathVariable Long id) {
		ExpertEstimateResponse estimateResponse = expertEstimateService.findById(id);

		return ResponseEntity.ok(estimateResponse);
	}

	@Operation(summary = "고수 견적서 삭제", description = "고수 견적서를 삭제합니다.", tags = {"ExpertEstimateController"})
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(
		@Parameter(description = "고수 견적서 ID", required = true)
		@PathVariable Long id) {
		expertEstimateService.delete(id);

		return ResponseEntity.ok(null);
	}

	@Operation(summary = "회원 견적서 ID로 고수 견적서 목록 조회", description = "회원 견적서 ID로 고수 견적서 목록을 조회합니다.", tags = {"ExpertEstimateController"})
	@GetMapping("/member-estimates/{memberEstimateId}")
	public ResponseEntity<ExpertEstimatesResponse> findAllByMemberEstimateId(
		@Parameter(description = "회원 요청 견적서 ID", required = true)
		@PathVariable Long memberEstimateId) {
		ExpertEstimatesResponse expertEstimatesResponse = expertEstimateService.findAllByMemberEstimateId(memberEstimateId);

		return ResponseEntity.ok(expertEstimatesResponse);
	}

	@Operation(summary = "고수 ID로 매칭되지 않은 고수 바로 견적서 목록 조회", description = "고수 ID로 매칭되지 않은 고수 바로 견적서 목록을 조회합니다.", tags = {"ExpertEstimateController"})
	@GetMapping("/auto")
	@CurrentExpertId
	public ResponseEntity<ExpertAutoEstimatesResponse> findAllUnmatchedAutoByExpertId(
		@Parameter(description = "고수 ID", required = true)
		Long expertId) {
		ExpertAutoEstimatesResponse expertAutoEstimatesResponse = expertEstimateService.findAllUnmatchedAutoByExpertId(expertId);

		return ResponseEntity.ok(expertAutoEstimatesResponse);
	}
}
