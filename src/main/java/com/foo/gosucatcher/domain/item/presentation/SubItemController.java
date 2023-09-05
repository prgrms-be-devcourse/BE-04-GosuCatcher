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

import com.foo.gosucatcher.domain.item.application.SubItemService;
import com.foo.gosucatcher.domain.item.application.dto.request.SubItemCreateRequest;
import com.foo.gosucatcher.domain.item.application.dto.request.SubItemUpdateRequest;
import com.foo.gosucatcher.domain.item.application.dto.response.SubItemResponse;
import com.foo.gosucatcher.domain.item.application.dto.response.SubItemsResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sub-items")
public class SubItemController {

	private final SubItemService subItemService;

	@PostMapping
	public ResponseEntity<SubItemResponse> create(@Validated @RequestBody SubItemCreateRequest request) {

		SubItemResponse subItemResponse = subItemService.create(request);

		return ResponseEntity.ok(subItemResponse);
	}

	@GetMapping("/{id}")
	public ResponseEntity<SubItemResponse> findOne(@PathVariable Long id) {
		SubItemResponse subItemResponse = subItemService.findById(id);

		return ResponseEntity.ok(subItemResponse);
	}

	@GetMapping
	public ResponseEntity<SubItemsResponse> findAll() {
		SubItemsResponse subItemsResponse = subItemService.findAll();

		return ResponseEntity.ok(subItemsResponse);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<Long> update(@PathVariable Long id,
		@Validated @RequestBody SubItemUpdateRequest subItemUpdateRequest) {

		Long subItemId = subItemService.update(id, subItemUpdateRequest);

		return ResponseEntity.ok(subItemId);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		subItemService.delete(id);

		return ResponseEntity.ok(null);
	}
}
