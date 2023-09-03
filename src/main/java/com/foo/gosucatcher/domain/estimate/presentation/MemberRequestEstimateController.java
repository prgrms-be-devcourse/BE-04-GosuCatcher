package com.foo.gosucatcher.domain.estimate.presentation;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foo.gosucatcher.domain.estimate.application.dto.request.MemberRequestEstimateRequest;
import com.foo.gosucatcher.domain.estimate.application.dto.response.MemberRequestEstimateResponse;
import com.foo.gosucatcher.domain.estimate.application.dto.response.MemberRequestEstimatesResponse;
import com.foo.gosucatcher.domain.estimate.application.MemberRequestEstimateService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/member-request-estimates")
@RestController
public class MemberRequestEstimateController {

	private final MemberRequestEstimateService memberRequestEstimateService;

	@PostMapping
	public ResponseEntity<MemberRequestEstimateResponse> create(@RequestParam("member") Long memberId,
		@RequestParam("subItem") Long subItemId,
		@RequestBody @Valid MemberRequestEstimateRequest memberRequestEstimateRequest) {
		MemberRequestEstimateResponse memberRequestEstimateResponse = memberRequestEstimateService.create(memberId,
			subItemId, memberRequestEstimateRequest);

		return ResponseEntity.ok(memberRequestEstimateResponse);
	}

	@GetMapping
	public ResponseEntity<MemberRequestEstimatesResponse> findAll(@RequestParam(value = "member") Long memberId) {
		return ResponseEntity.ok(memberRequestEstimateService.findAllByMember(memberId));
	}

	@GetMapping("/{memberRequestEstimateId}")
	public ResponseEntity<MemberRequestEstimateResponse> findById(@PathVariable Long memberRequestEstimateId) {
		MemberRequestEstimateResponse memberRequestEstimateResponse = memberRequestEstimateService.findById(
			memberRequestEstimateId);

		return ResponseEntity.ok(memberRequestEstimateResponse);
	}

	@PatchMapping("/{memberRequestEstimateId}")
	public ResponseEntity<MemberRequestEstimateResponse> update(@PathVariable Long memberRequestEstimateId,
		@RequestBody @Valid MemberRequestEstimateRequest memberRequestEstimateRequest) {
		MemberRequestEstimateResponse memberRequestEstimateResponse = memberRequestEstimateService.update(
			memberRequestEstimateId, memberRequestEstimateRequest);

		return ResponseEntity.ok(memberRequestEstimateResponse);
	}

	@DeleteMapping("/{memberRequestEstimateId}")
	public ResponseEntity<Void> delete(@PathVariable Long memberRequestEstimateId) {
		memberRequestEstimateService.delete(memberRequestEstimateId);

		return ResponseEntity.ok(null);
	}
}
