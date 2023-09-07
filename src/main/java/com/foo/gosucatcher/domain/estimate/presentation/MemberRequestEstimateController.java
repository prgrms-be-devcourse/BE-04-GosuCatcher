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

import com.foo.gosucatcher.domain.estimate.application.MemberRequestEstimateService;
import com.foo.gosucatcher.domain.estimate.application.dto.request.MemberRequestEstimateRequest;
import com.foo.gosucatcher.domain.estimate.application.dto.response.MemberRequestEstimateResponse;
import com.foo.gosucatcher.domain.estimate.application.dto.response.MemberRequestEstimatesResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/member-request-estimates")
@RestController
public class MemberRequestEstimateController {

	private final MemberRequestEstimateService memberRequestEstimateService;

	@PostMapping("/{memberId}")
	public ResponseEntity<MemberRequestEstimateResponse> create(@PathVariable Long memberId,
		@Validated @RequestBody MemberRequestEstimateRequest memberRequestEstimateRequest) {
		MemberRequestEstimateResponse memberRequestEstimateResponse = memberRequestEstimateService.create(memberId,
			memberRequestEstimateRequest);

		return ResponseEntity.ok(memberRequestEstimateResponse);
	}

	@GetMapping
	public ResponseEntity<MemberRequestEstimatesResponse> findAll() {
		MemberRequestEstimatesResponse memberRequestEstimatesResponse = memberRequestEstimateService.findAll();

		return ResponseEntity.ok(memberRequestEstimatesResponse);
	}

	@GetMapping("/members/{memberId}")
	public ResponseEntity<MemberRequestEstimatesResponse> findAllByMember(@PathVariable Long memberId) {
		MemberRequestEstimatesResponse memberRequestEstimatesResponse = memberRequestEstimateService.findAllByMember(memberId);

		return ResponseEntity.ok(memberRequestEstimatesResponse);
	}

	@GetMapping("/{memberRequestEstimateId}")
	public ResponseEntity<MemberRequestEstimateResponse> findById(@PathVariable Long memberRequestEstimateId) {
		MemberRequestEstimateResponse memberRequestEstimateResponse = memberRequestEstimateService.findById(
			memberRequestEstimateId);

		return ResponseEntity.ok(memberRequestEstimateResponse);
	}

	@PatchMapping("/{memberRequestEstimateId}")
	public ResponseEntity<Long> update(@PathVariable Long memberRequestEstimateId,
		@RequestBody @Validated MemberRequestEstimateRequest memberRequestEstimateRequest) {
		Long updatedMemberRequestEstimateId = memberRequestEstimateService.update(memberRequestEstimateId,
			memberRequestEstimateRequest);

		return ResponseEntity.ok(updatedMemberRequestEstimateId);
	}

	@DeleteMapping("/{memberRequestEstimateId}")
	public ResponseEntity<Void> delete(@PathVariable Long memberRequestEstimateId) {
		memberRequestEstimateService.delete(memberRequestEstimateId);

		return ResponseEntity.ok(null);
	}
}
