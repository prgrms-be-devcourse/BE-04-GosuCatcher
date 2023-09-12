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

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/member-estimates")
@RestController
public class MemberEstimateController {

	private final MemberEstimateService memberEstimateService;
	private final MatchingService matchingService;

	@PostMapping("/normal/{memberId}")
	public ResponseEntity<MemberEstimateResponse> createNormal(@PathVariable Long memberId,
															   @Validated @RequestBody MemberEstimateRequest memberEstimateRequest) {
		MemberEstimateResponse memberEstimateResponse = memberEstimateService.create(memberId, memberEstimateRequest);

		return ResponseEntity.ok(memberEstimateResponse);
	}

	@PostMapping("/auto/{memberId}")
	public ResponseEntity<MessagesResponse> createAuto(@PathVariable Long memberId,
													   @Validated @RequestBody MemberEstimateRequest memberEstimateRequest) {
		MemberEstimateResponse memberEstimateResponse = memberEstimateService.create(memberId, memberEstimateRequest);

		MessagesResponse messagesResponse = matchingService.match(memberEstimateResponse);

		return ResponseEntity.ok(messagesResponse);
	}

	@GetMapping
	public ResponseEntity<MemberEstimatesResponse> findAll() {
		MemberEstimatesResponse memberEstimatesResponse = memberEstimateService.findAll();

		return ResponseEntity.ok(memberEstimatesResponse);
	}

	@GetMapping("/members/{memberId}")
	public ResponseEntity<MemberEstimatesResponse> findAllByMember(@PathVariable Long memberId) {
		MemberEstimatesResponse memberEstimatesResponse = memberEstimateService.findAllByMember(memberId);

		return ResponseEntity.ok(memberEstimatesResponse);
	}

	@GetMapping("/{memberEstimateId}")
	public ResponseEntity<MemberEstimateResponse> findById(@PathVariable Long memberEstimateId) {
		MemberEstimateResponse memberEstimateResponse = memberEstimateService.findById(memberEstimateId);

		return ResponseEntity.ok(memberEstimateResponse);
	}

	@DeleteMapping("/{memberEstimateId}")
	public ResponseEntity<Void> delete(@PathVariable Long memberEstimateId) {
		memberEstimateService.delete(memberEstimateId);

		return ResponseEntity.ok(null);
	}
}
