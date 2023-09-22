package com.foo.gosucatcher.domain.item.presentation;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

import com.foo.gosucatcher.domain.item.application.SubItemService;
import com.foo.gosucatcher.domain.item.application.dto.request.sub.SubItemCreateRequest;
import com.foo.gosucatcher.domain.item.application.dto.request.sub.SubItemUpdateRequest;
import com.foo.gosucatcher.domain.item.application.dto.response.sub.SubItemResponse;
import com.foo.gosucatcher.domain.item.application.dto.response.sub.SubItemsResponse;
import com.foo.gosucatcher.domain.item.application.dto.response.sub.SubItemsSliceResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "SubItemController", description = "세부 서비스 등록,조회,수정,삭제")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sub-items")
public class SubItemController {

	private final SubItemService subItemService;

	@Operation(summary = "세부 서비스 등록", description = "세부 서비스가 추가 됩니다.")
	@PostMapping
	public ResponseEntity<SubItemResponse> create(@Parameter(description = "서비스 등록 요청 정보", required = true)
												  @Validated @RequestBody SubItemCreateRequest request) {

		SubItemResponse subItemResponse = subItemService.create(request);

		return ResponseEntity.ok(subItemResponse);
	}

	@Operation(summary = "세부 서비스 ID로 조회", description = "세부 서비스를 ID로 조회 합니다.")
	@GetMapping("/{id}")
	public ResponseEntity<SubItemResponse> findOne(@Parameter(description = "찾을 세부 서비스 ID", required = true, example = "1")
												   @PathVariable Long id) {
		SubItemResponse subItemResponse = subItemService.findById(id);

		return ResponseEntity.ok(subItemResponse);
	}

	@Operation(summary = "세부 서비스 전체 조회", description = "세부 서비스를 전체 조회 합니다.")
	@GetMapping
	public ResponseEntity<SubItemsResponse> findAll() {
		SubItemsResponse subItemsResponse = subItemService.findAll();

		return ResponseEntity.ok(subItemsResponse);
	}

	@GetMapping("/mainItem")
	@Operation(summary = "해당 메인 서비스에 속한 세부 서비스들 조회", description = "특정 메인 서비스에 속한 세부 서비스들을 조회 합니다.")
	public ResponseEntity<SubItemsSliceResponse> findSubItemsByMainItemName(@Parameter(description = "메인 서비스 이름", required = true)
																			@RequestParam String mainItemName, @Parameter(description = "페이징 초기값", required = false) @PageableDefault(page = 0, size = 10) Pageable pageable) {
		SubItemsSliceResponse sliceResponse = subItemService.findAllByMainItemName(mainItemName, pageable);

		return ResponseEntity.ok(sliceResponse);
	}

	@Operation(summary = "세부 서비스 수정", description = "세부 서비스 정보를 수정합니다.")
	@PatchMapping("/{id}")
	public ResponseEntity<Long> update(@Parameter(description = "수정할 세부 서비스 id", required = true) @PathVariable Long id,
									   @Parameter(description = "수정 요청 정보", required = true) @Validated @RequestBody SubItemUpdateRequest subItemUpdateRequest) {

		Long subItemId = subItemService.update(id, subItemUpdateRequest);

		return ResponseEntity.ok(subItemId);
	}

	@Operation(summary = "세부 서비스 삭제", description = "세부 서비스를 삭제 합니다.")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@Parameter(description = "삭제할 서비스 id", required = true)
									   @PathVariable Long id) {
		subItemService.delete(id);

		return ResponseEntity.ok(null);
	}
}
