package com.foo.gosucatcher.domain.item.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foo.gosucatcher.domain.item.application.MainItemService;
import com.foo.gosucatcher.domain.item.application.dto.request.MainItemCreateRequest;
import com.foo.gosucatcher.domain.item.application.dto.request.MainItemUpdateRequest;
import com.foo.gosucatcher.domain.item.application.dto.response.MainItemResponse;
import com.foo.gosucatcher.domain.item.application.dto.response.MainItemsResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/main-items")
public class MainItemController {

	private final MainItemService mainItemService;

	@PostMapping
	public ResponseEntity<MainItemResponse> create(@Validated @RequestBody MainItemCreateRequest request) {
		MainItemResponse mainItemResponse = mainItemService.create(request);

		return ResponseEntity.ok(mainItemResponse);
	}

	@GetMapping
	public ResponseEntity<MainItemsResponse> findAll() {
		MainItemsResponse mainItemsResponse = mainItemService.findAll();

		return ResponseEntity.ok(mainItemsResponse);
	}

	@GetMapping("/{id}")
	public ResponseEntity<MainItemResponse> findOne(@PathVariable Long id) {
		MainItemResponse mainItemResponse = mainItemService.findById(id);

		return ResponseEntity.ok(mainItemResponse);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<Long> update(@PathVariable Long id, @Validated @RequestBody MainItemUpdateRequest request) {
		Long updatedId = mainItemService.update(id, request);

		return ResponseEntity.ok(updatedId);
	}
}