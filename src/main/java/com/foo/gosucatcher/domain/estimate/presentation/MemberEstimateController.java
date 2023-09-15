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

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/member-estimates")
@RestController
public class MemberEstimateController {

	private final MemberEstimateService memberEstimateService;
	private final MatchingService matchingService;

	@PostMapping("/normal/{expertId}")
	@CurrentMemberId
	public ResponseEntity<MemberEstimateResponse> createNormal(Long memberId, @PathVariable Long expertId,
															   @Validated @RequestBody MemberEstimateRequest memberEstimateRequest) {
		MemberEstimateResponse memberEstimateResponse = memberEstimateService.createNormal(memberId, expertId, memberEstimateRequest);

		return ResponseEntity.ok(memberEstimateResponse);
	}

	@PostMapping("/auto")
	@CurrentMemberId
	public ResponseEntity<MessagesResponse> createAuto(Long memberId,
													   @Validated @RequestBody MemberEstimateRequest memberEstimateRequest) {
		MemberEstimateResponse memberEstimateResponse = memberEstimateService.createAuto(memberId, memberEstimateRequest);

		MessagesResponse messagesResponse = matchingService.match(memberEstimateResponse);

		return ResponseEntity.ok(messagesResponse);
	}

	@GetMapping
	public ResponseEntity<MemberEstimatesResponse> findAll() {
		MemberEstimatesResponse memberEstimatesResponse = memberEstimateService.findAll();

		return ResponseEntity.ok(memberEstimatesResponse);
	}

	@GetMapping("/members")
	@CurrentMemberId
	public ResponseEntity<MemberEstimatesResponse> findAllByMemberId(Long memberId) {
		MemberEstimatesResponse memberEstimatesResponse = memberEstimateService.findAllByMemberId(memberId);

		return ResponseEntity.ok(memberEstimatesResponse);
	}

	@GetMapping("/{memberEstimateId}")
	public ResponseEntity<MemberEstimateResponse> findById(@PathVariable Long memberEstimateId) {
		MemberEstimateResponse memberEstimateResponse = memberEstimateService.findById(memberEstimateId);

		return ResponseEntity.ok(memberEstimateResponse);
	}

	@GetMapping("/normal")
	@CurrentExpertId
	public ResponseEntity<MemberEstimatesResponse> findAllPendingNormalByExpertId(Long expertId) {
		MemberEstimatesResponse memberEstimatesResponse = memberEstimateService.findAllPendingNormalByExpertId(expertId);

		return ResponseEntity.ok(memberEstimatesResponse);
	}

	@DeleteMapping("/{memberEstimateId}")
	public ResponseEntity<Void> delete(@PathVariable Long memberEstimateId) {
		memberEstimateService.delete(memberEstimateId);

		return ResponseEntity.ok(null);
	}
}
