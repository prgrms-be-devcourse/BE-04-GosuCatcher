package com.foo.gosucatcher.domain.bucket.presentation;

import java.net.URI;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.foo.gosucatcher.domain.bucket.application.BucketService;
import com.foo.gosucatcher.domain.bucket.dto.request.BucketRequest;
import com.foo.gosucatcher.domain.bucket.dto.response.BucketResponse;
import com.foo.gosucatcher.domain.bucket.dto.response.BucketsResponse;
import com.foo.gosucatcher.global.aop.CurrentMemberId;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/buckets")
@Tag(name = "BucketController", description = "찜 기능 API")
public class BucketController {

	private final BucketService bucketService;

	@Operation(summary = "찜한 기록 조회", description = "특정 고수에 대해 누가 찜했는지 그 기록 조회", tags = {"BucketController"})
	@GetMapping
	public ResponseEntity<BucketsResponse> findAll(Pageable pageable) {
		BucketsResponse bucketsResponse = bucketService.findAll(pageable);

		return ResponseEntity.ok(bucketsResponse);
	}

	@Operation(summary = "특정 사용자가 찜한 고수 목록 조회", tags = {"BucketController"})
	@GetMapping("/members")
	@CurrentMemberId
	public ResponseEntity<BucketsResponse> findAllByMemberId(
		@Parameter(description = "사용자 ID")
		@RequestParam Long memberId,

		Pageable pageable
	) {
		BucketsResponse bucketsResponse = bucketService.findAllByMemberId(memberId, pageable);

		return ResponseEntity.ok(bucketsResponse);
	}

	@Operation(summary = "찜 취소", description = "특정 사용자가 고수에 대해 찜을 취소 ", tags = {"BucketController"})
	@DeleteMapping("/{id}")
	public ResponseEntity<Object> delete(
		@Parameter(description = "취소하고자 하는 찜 ID")
		@PathVariable Long id
	) {
		bucketService.deleteById(id);

		return ResponseEntity.noContent()
			.build();
	}

	@Operation(summary = "찜 하기", description = "특정 사용자가 고수에 대해 찜 하기", tags = {"BucketController"})
	@PostMapping
	public ResponseEntity<BucketResponse> create(@RequestBody BucketRequest bucketRequest) {
		BucketResponse bucketResponse = bucketService.create(bucketRequest);
		URI uri = ServletUriComponentsBuilder
			.fromCurrentRequest()
			.path("/{memberId}")
			.buildAndExpand(bucketResponse.id())
			.toUri();

		return ResponseEntity.created(uri)
			.body(bucketResponse);
	}

	@Operation(summary = "찜 여부 조회", description = "특정 고수에 대해 사용자가 찜했는지 여부 조회", tags = {"BucketController"})
	@GetMapping("/status")
	public ResponseEntity<Boolean> checkStatus(
		@Parameter(description = "고수 ID")
		@RequestParam Long expertId,

		@Parameter(description = "사용자 ID")
		@RequestParam Long memberId) {
		Boolean status = bucketService.checkStatus(expertId, memberId);

		return ResponseEntity.ok(status);
	}
}
