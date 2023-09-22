package com.foo.gosucatcher.domain.item.presentation;

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

import com.foo.gosucatcher.domain.item.application.MainItemService;
import com.foo.gosucatcher.domain.item.application.dto.request.main.MainItemCreateRequest;
import com.foo.gosucatcher.domain.item.application.dto.request.main.MainItemUpdateRequest;
import com.foo.gosucatcher.domain.item.application.dto.response.main.MainItemResponse;
import com.foo.gosucatcher.domain.item.application.dto.response.main.MainItemsResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "메인 서비스 관련 Controller", description = "메인 서비스 등록,조회,수정,삭제")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/main-items")
public class MainItemController {

	private final MainItemService mainItemService;

	@Operation(summary = "메인 서비스 등록", description = "메인 서비스가 추가됩니다.")
	@PostMapping
	public ResponseEntity<MainItemResponse> create(@Parameter(description = "서비스 등록 요청 정보", required = true) @Validated @RequestBody MainItemCreateRequest request) {
		MainItemResponse mainItemResponse = mainItemService.create(request);

		return ResponseEntity.ok(mainItemResponse);
	}

	@Operation(summary = "메인 서비스 전체 조회", description = "메인 서비스를 전체 조회할 수 있습니다.")
	@GetMapping
	public ResponseEntity<MainItemsResponse> findAll() {
		MainItemsResponse mainItemsResponse = mainItemService.findAll();

		return ResponseEntity.ok(mainItemsResponse);
	}

	@Operation(summary = "메인 서비스를 ID로 조회", description = "메인 서비스를 ID로 조회 할 수 있습니다.")
	@GetMapping("/{id}")
	public ResponseEntity<MainItemResponse> findOne(@Parameter(description = "찾을 서비스 ID", required = true, example = "1")
													@PathVariable Long id) {
		MainItemResponse mainItemResponse = mainItemService.findById(id);

		return ResponseEntity.ok(mainItemResponse);
	}

	@Operation(summary = "메인 서비스 수정", description = "메인 서비스를 수정 할 수 있습니다.")
	@PatchMapping("/{id}")
	public ResponseEntity<MainItemResponse> update(@Parameter(description = "수정할 메인 서비스 ID", required = true, example = "1") @PathVariable Long id, @Parameter(description = "수정할 서비스 정보", required = true) @Validated @RequestBody MainItemUpdateRequest request) {
		MainItemResponse mainItemResponse = mainItemService.update(id, request);

		return ResponseEntity.ok(mainItemResponse);
	}

	@Operation(summary = "메인 서비스 삭제", description = "메인 서비스를 삭제 할 수 있습니다.")
	@DeleteMapping("{id}")
	public ResponseEntity<Void> delete(@Parameter(description = "삭제할 메인 서비스 ID") @PathVariable Long id) {
		mainItemService.delete(id);

		return ResponseEntity.ok(null);
	}
}
