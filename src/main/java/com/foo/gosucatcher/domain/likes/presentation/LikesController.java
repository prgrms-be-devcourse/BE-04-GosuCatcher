package com.foo.gosucatcher.domain.likes.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foo.gosucatcher.domain.likes.application.LikesService;
import com.foo.gosucatcher.domain.likes.dto.request.LikesRequest;
import com.foo.gosucatcher.domain.likes.dto.response.LikesResponse;
import com.foo.gosucatcher.domain.likes.dto.response.LikesResponses;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/likes")
public class LikesController {

	private final LikesService likesService;

	@GetMapping
	public ResponseEntity<LikesResponses> findAll() {
		LikesResponses likesResponses = likesService.findAll();

		return ResponseEntity.ok(likesResponses);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		likesService.deleteById(id);

		return ResponseEntity.ok(null);
	}

	@PostMapping
	public ResponseEntity<LikesResponse> create(@RequestBody LikesRequest likesRequest) {
		LikesResponse likesResponse = likesService.create(likesRequest);

		return ResponseEntity.ok(likesResponse);
	}

	@GetMapping("/{status}")
	public ResponseEntity<Boolean> checkStatus(@RequestParam Long expertId, @RequestParam Long memberId) {
		Boolean status = likesService.checkStatus(expertId, memberId);

		return ResponseEntity.ok(status);
	}
}
