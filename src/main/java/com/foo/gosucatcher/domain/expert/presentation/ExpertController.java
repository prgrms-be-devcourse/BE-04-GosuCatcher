package com.foo.gosucatcher.domain.expert.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foo.gosucatcher.domain.expert.application.ExpertService;
import com.foo.gosucatcher.domain.expert.application.dto.request.ExpertCreateRequest;
import com.foo.gosucatcher.domain.expert.application.dto.request.ExpertUpdateRequest;
import com.foo.gosucatcher.domain.expert.application.dto.response.ExpertResponse;
import com.foo.gosucatcher.domain.expert.application.dto.response.ExpertsResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/experts")
public class ExpertController {

	private final ExpertService expertService;

	@PostMapping
	public ResponseEntity<ExpertResponse> create(@Validated @RequestBody ExpertCreateRequest request,
		@RequestParam Long memberId) {
		ExpertResponse expertResponse = expertService.create(request, memberId);

		return ResponseEntity.ok(expertResponse);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ExpertResponse> findOne(@PathVariable Long id) {
		ExpertResponse expert = expertService.findById(id);

		return ResponseEntity.ok(expert);
	}

	@GetMapping
	public ResponseEntity<ExpertsResponse> findAll() {
		ExpertsResponse experts = expertService.findAll();

		return ResponseEntity.ok(experts);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<Long> update(@PathVariable Long id, @Validated @RequestBody ExpertUpdateRequest request) {
		Long expertId = expertService.update(id, request);

		return ResponseEntity.ok(expertId);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		expertService.delete(id);

		return ResponseEntity.noContent().build();
	}
}