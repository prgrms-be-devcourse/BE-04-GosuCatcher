package com.foo.gosucatcher.domain.estimate.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foo.gosucatcher.domain.chat.application.dto.response.MessagesResponse;
import com.foo.gosucatcher.domain.estimate.application.MemberEstimateService;
import com.foo.gosucatcher.domain.estimate.application.dto.request.MemberEstimateRequest;
import com.foo.gosucatcher.domain.estimate.application.dto.response.MemberEstimateResponse;
import com.foo.gosucatcher.domain.estimate.application.dto.response.MemberEstimatesResponse;
import com.foo.gosucatcher.domain.matching.application.MatchingService;
import com.foo.gosucatcher.global.aop.CurrentExpertId;
import com.foo.gosucatcher.global.aop.CurrentMemberId;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "MemberEstimateController", description = "일반 견적 요청서, 바로 견적서 조회/생성/삭제 API")
@RequiredArgsConstructor
@RequestMapping("/api/v1/member-estimates")
@RestController
public class MemberEstimateController {

	private final MemberEstimateService memberEstimateService;
	private final MatchingService matchingService;

	@Operation(summary = "회원 일반 견적 요청서 생성", description = "회원 일반 견적 요청서를 생성합니다.", tags = {"MemberEstimateController"})
	@PostMapping("/normal/{expertId}")
	@CurrentMemberId
	public ResponseEntity<MemberEstimateResponse> createNormal(
		@Parameter(description = "회원 ID", required = true)
		Long memberId,

		@Parameter(description = "고수 ID", required = true)
		@PathVariable Long expertId,

		@Parameter(description = "회원 견적서 생성 정보", required = true)
		@Validated @RequestBody MemberEstimateRequest memberEstimateRequest) {
		MemberEstimateResponse memberEstimateResponse = memberEstimateService.createNormal(memberId, expertId,
			memberEstimateRequest);

		return ResponseEntity.ok(memberEstimateResponse);
	}

	@Operation(summary = "회원 바로 견적 요청서 생성", description = "회원 바로 견적 요청서를 생성합니다.", tags = {"MemberEstimateController"})
	@PostMapping("/auto")
	@CurrentMemberId
	public ResponseEntity<MessagesResponse> createAuto(
		@Parameter(description = "회원 ID", required = true)
		Long memberId,

		@Parameter(description = "회원 견적서 생성 정보", required = true)
		@Validated @RequestBody MemberEstimateRequest memberEstimateRequest) {
		MemberEstimateResponse memberEstimateResponse = memberEstimateService.createAuto(memberId,
			memberEstimateRequest);

		MessagesResponse messagesResponse = matchingService.match(memberEstimateResponse);

		return ResponseEntity.ok(messagesResponse);
	}

	@Operation(summary = "모든 회원 견적 요청서 조회", description = "모든 회원 바로 견적 요청서를 조회합니다.", tags = {"MemberEstimateController"})
	@GetMapping
	public ResponseEntity<MemberEstimatesResponse> findAll() {
		MemberEstimatesResponse memberEstimatesResponse = memberEstimateService.findAll();

		return ResponseEntity.ok(memberEstimatesResponse);
	}

	@Operation(summary = "회원 ID로 회원 견적 요청서 조회", description = "회원 ID로 회원 견적 요청서를 조회합니다.", tags = {"MemberEstimateController"})
	@GetMapping("/members")
	@CurrentMemberId
	public ResponseEntity<MemberEstimatesResponse> findAllByMemberId(
		@Parameter(description = "회원 ID", required = true)
		Long memberId) {
		MemberEstimatesResponse memberEstimatesResponse = memberEstimateService.findAllByMemberId(memberId);

		return ResponseEntity.ok(memberEstimatesResponse);
	}

	@Operation(summary = "회원 견적서 ID로 회원 견적 요청서 조회", description = "회원 견적서 ID로 회원 견적 요청서를 조회합니다.", tags = {"MemberEstimateController"})
	@GetMapping("/{memberEstimateId}")
	public ResponseEntity<MemberEstimateResponse> findById(
		@Parameter(description = "회원 견적서 ID", required = true)
		@PathVariable Long memberEstimateId) {
		MemberEstimateResponse memberEstimateResponse = memberEstimateService.findById(memberEstimateId);

		return ResponseEntity.ok(memberEstimateResponse);
	}

	@Operation(summary = "고수 ID로 대기중인 일반 견적 요청서 조회", description = "고수 ID로 대기중인 일반 견적 요청서를 조회합니다.", tags = {"MemberEstimateController"})
	@GetMapping("/normal")
	@CurrentExpertId
	public ResponseEntity<MemberEstimatesResponse> findAllPendingNormalByExpertId(
		@Parameter(description = "고수 ID", required = true)
		Long expertId) {
		MemberEstimatesResponse memberEstimatesResponse = memberEstimateService.findAllPendingNormalByExpertId(
			expertId);

		return ResponseEntity.ok(memberEstimatesResponse);
	}

	@Operation(summary = "회원 견적 요청서 삭제", description = "회원 견적 요청서를 삭제합니다.", tags = {"MemberEstimateController"})
	@DeleteMapping("/{memberEstimateId}")
	public ResponseEntity<Void> delete(
		@Parameter(description = "회원 견적서 ID", required = true)
		@PathVariable Long memberEstimateId) {
		memberEstimateService.delete(memberEstimateId);

		return ResponseEntity.ok(null);
	}
}
