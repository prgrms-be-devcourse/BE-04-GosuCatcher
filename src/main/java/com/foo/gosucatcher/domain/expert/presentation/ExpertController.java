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
import com.foo.gosucatcher.domain.expert.application.dto.request.ExpertSubItemRequest;
import com.foo.gosucatcher.domain.expert.application.dto.request.ExpertUpdateRequest;
import com.foo.gosucatcher.domain.expert.application.dto.response.ExpertResponse;
import com.foo.gosucatcher.domain.expert.application.dto.response.SlicedExpertsResponse;
import com.foo.gosucatcher.domain.expert.domain.SortType;
import com.foo.gosucatcher.domain.image.application.dto.request.ImageUploadRequest;
import com.foo.gosucatcher.domain.image.application.dto.response.ImageResponse;
import com.foo.gosucatcher.domain.image.application.dto.response.ImagesResponse;
import com.foo.gosucatcher.domain.item.application.dto.response.sub.SubItemsResponse;
import com.foo.gosucatcher.global.aop.CurrentExpertId;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/experts")
public class ExpertController {

	private final ExpertService expertService;

	@CurrentExpertId
	@PostMapping
	public ResponseEntity<ExpertResponse> create(Long expertId, @Validated @RequestBody ExpertUpdateRequest request) {
		ExpertResponse expertResponse = expertService.create(expertId, request);
		return ResponseEntity.ok(expertResponse);
	}

	@CurrentExpertId
	@GetMapping
	public ResponseEntity<ExpertResponse> findOne(Long expertId) {

		ExpertResponse expert = expertService.findById(expertId);

		return ResponseEntity.ok(expert);
	}

	@CurrentExpertId
	@PatchMapping
	public ResponseEntity<Long> update(Long expertId,
		@Validated @RequestBody ExpertUpdateRequest request) {
		Long updatedExpertId = expertService.update(expertId, request);

		return ResponseEntity.ok(updatedExpertId);
	}

	@CurrentExpertId
	@DeleteMapping
	public ResponseEntity<Void> delete(Long expertId) {
		expertService.delete(expertId);

		return ResponseEntity.ok(null);
	}

	@CurrentExpertId
	@PostMapping("/sub-items")
	public ResponseEntity<Long> addSubItem(Long expertId, @RequestBody ExpertSubItemRequest request) {
		Long id = expertService.addSubItem(expertId, request);

		return ResponseEntity.ok(id);
	}

	@CurrentExpertId
	@DeleteMapping("/sub-items")
	public ResponseEntity<Object> removeItem(Long expertId, @RequestBody ExpertSubItemRequest request) {
		expertService.removeSubItem(expertId, request);

		return ResponseEntity.noContent()
			.build();
	}

	@CurrentExpertId
	@GetMapping("/sub-items")
	public ResponseEntity<SubItemsResponse> getSubItemsByExpertId(Long expertId) {
		SubItemsResponse response = expertService.getSubItemsByExpertId(expertId);

		return ResponseEntity.ok(response);
	}

	@CurrentExpertId
	@PostMapping("/images")
	public ResponseEntity<ImagesResponse> uploadImage(Long expertId, MultipartFile file) throws IOException {
		ImageUploadRequest request = new ImageUploadRequest(List.of(file));
		ImagesResponse response = expertService.uploadImage(expertId, request);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@CurrentExpertId
	@DeleteMapping("/images/{filename}")
	public ResponseEntity<String> deleteImage(Long expertId, @PathVariable String filename) {
		expertService.deleteImage(expertId, filename);

		return ResponseEntity.ok(null);
	}

	@CurrentExpertId
	@GetMapping("/images")
	public ResponseEntity<ImageResponse> getAllImages(Long expertId) {
		ImageResponse response = expertService.getAllImages(expertId);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/search")
	public ResponseEntity<SlicedExpertsResponse> searchExperts(
		@RequestParam(required = false) String subItem,
		@RequestParam(required = false) String location,
		@PageableDefault(sort = {"reviewCount"}, direction = Sort.Direction.DESC) Pageable pageable) {
		SortType.validateSortColumns(pageable.getSort());

		return ResponseEntity.ok(expertService.findExperts(subItem, location, pageable));
	}
}
