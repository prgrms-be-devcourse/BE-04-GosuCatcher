package com.foo.gosucatcher.domain.expert.presentation;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.multipart.MultipartFile;

import com.foo.gosucatcher.domain.expert.application.ExpertService;
import com.foo.gosucatcher.domain.expert.application.dto.request.ExpertCreateRequest;
import com.foo.gosucatcher.domain.expert.application.dto.request.ExpertSubItemRequest;
import com.foo.gosucatcher.domain.expert.application.dto.request.ExpertUpdateRequest;
import com.foo.gosucatcher.domain.expert.application.dto.response.ExpertResponse;
import com.foo.gosucatcher.domain.expert.application.dto.response.SlicedExpertsResponse;
import com.foo.gosucatcher.domain.expert.domain.SortType;
import com.foo.gosucatcher.domain.image.ImageService;
import com.foo.gosucatcher.domain.image.application.dto.request.ImageUploadRequest;
import com.foo.gosucatcher.domain.image.application.dto.response.ImagesResponse;
import com.foo.gosucatcher.domain.item.application.dto.response.sub.SubItemsResponse;
import com.foo.gosucatcher.global.aop.CurrentExpertId;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/experts")
public class ExpertController {

	private final ExpertService expertService;
	private final ImageService imageService;

	@CurrentExpertId
	@PostMapping
	public ResponseEntity<ExpertResponse> create(@Validated @RequestBody ExpertCreateRequest request,
		@RequestParam Long memberId) {
		ExpertResponse expertResponse = expertService.create(request, memberId);

		return ResponseEntity.ok(expertResponse);
	}

	@CurrentExpertId
	@GetMapping("/{expertId}")
	public ResponseEntity<ExpertResponse> findOne(@PathVariable Long expertId) {
		ExpertResponse expert = expertService.findById(expertId);

		return ResponseEntity.ok(expert);
	}

	@CurrentExpertId
	@PatchMapping("/{expertId}")
	public ResponseEntity<Long> update(@PathVariable Long expertId,
		@Validated @RequestBody ExpertUpdateRequest request) {
		Long updatedExpertId = expertService.update(expertId, request);

		return ResponseEntity.ok(updatedExpertId);
	}

	@CurrentExpertId
	@DeleteMapping("/{expertId}")
	public ResponseEntity<Void> delete(@PathVariable Long expertId) {
		expertService.delete(expertId);

		return ResponseEntity.ok(null);
	}

	@CurrentExpertId
	@PostMapping("/{id}/sub-items")
	public ResponseEntity<Long> addSubItem(@PathVariable Long id, @RequestBody ExpertSubItemRequest request) {
		Long expertId = expertService.addSubItem(id, request);

		return ResponseEntity.ok(expertId);
	}

	@CurrentExpertId
	@DeleteMapping("/{id}/sub-items")
	public ResponseEntity<Object> removeItem(@PathVariable Long id, @RequestBody ExpertSubItemRequest request) {
		expertService.removeSubItem(id, request);

		return ResponseEntity.noContent()
			.build();
	}

	@CurrentExpertId
	@GetMapping("/{id}/sub-items")
	public ResponseEntity<SubItemsResponse> getSubItemsByExpertId(@PathVariable Long id) {
		SubItemsResponse response = expertService.getSubItemsByExpertId(id);

		return ResponseEntity.ok(response);
	}

	@CurrentExpertId
	@PostMapping("/{expertId}/images")
	public ResponseEntity<ImagesResponse> uploadImage(@PathVariable Long expertId, MultipartFile file) throws
		IOException {
		ImageUploadRequest request = new ImageUploadRequest(List.of(file));
		ImagesResponse response = expertService.uploadImage(expertId, request);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@CurrentExpertId
	@DeleteMapping("/{expertId}/images/{filename}")
	public ResponseEntity<String> deleteImage(@PathVariable Long expertId, @PathVariable String filename) {
		expertService.deleteImage(expertId, filename);

		return ResponseEntity.ok(null);
	}

	@CurrentExpertId
	@GetMapping("/{expertId}/images")
	public ResponseEntity<ImagesResponse> getAllImages(@PathVariable Long expertId) {
		ImagesResponse response = expertService.getAllImages(expertId);

		return ResponseEntity.ok(response);
	}

	@CurrentExpertId
	@GetMapping("/search")
	public ResponseEntity<SlicedExpertsResponse> searchExperts(
		@RequestParam(required = false) String subItem,
		@RequestParam(required = false) String location,
		@PageableDefault(sort = {"reviewCount"}, direction = Sort.Direction.DESC) Pageable pageable) {
		SortType.validateSortColumns(pageable.getSort());

		return ResponseEntity.ok(expertService.findExperts(subItem, location, pageable));
	}
}
