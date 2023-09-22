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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "고수 관련 Controller", description = "고수 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/experts")
public class ExpertController {

	private final ExpertService expertService;

	@CurrentExpertId
	@PostMapping
	@Operation(summary = "고수 생성", description = "고수 계정을 생성합니다.")
	public ResponseEntity<ExpertResponse> create(
		@Parameter(description = "고수 ID", required = true, example = "1")
		Long expertId,

		@Parameter(description = "고수 생성 요청", required = true)
		@Validated @RequestBody ExpertUpdateRequest request) {

		ExpertResponse expertResponse = expertService.create(expertId, request);
		return ResponseEntity.ok(expertResponse);
	}

	@CurrentExpertId
	@GetMapping
	@Operation(summary = "고수 조회", description = "고수 계정을 조회합니다.")
	public ResponseEntity<ExpertResponse> findOne(
		@Parameter(description = "고수 ID", required = true, example = "1")
		Long expertId) {

		ExpertResponse expert = expertService.findById(expertId);

		return ResponseEntity.ok(expert);
	}

	@CurrentExpertId
	@PatchMapping
	@Operation(summary = "고수 업데이트", description = "고수 계정을 업데이트합니다.")
	public ResponseEntity<Long> update(
		@Parameter(description = "고수 ID", required = true, example = "1")
		Long expertId,

		@Parameter(description = "고수 업데이트 요청", required = true)
		@Validated @RequestBody ExpertUpdateRequest request) {
		Long updatedExpertId = expertService.update(expertId, request);

		return ResponseEntity.ok(updatedExpertId);
	}

	@CurrentExpertId
	@DeleteMapping
	@Operation(summary = "고수 삭제", description = "고수 계정을 삭제합니다.")
	public ResponseEntity<Void> delete(
		@Parameter(description = "고수 ID", required = true, example = "1")
		Long expertId) {
		expertService.delete(expertId);

		return ResponseEntity.ok(null);
	}

	@CurrentExpertId
	@PostMapping("/sub-items")
	@Operation(summary = "고수 제공 서비스 추가", description = "고수 계정 내 제공 서비스를 추가합니다.")
	public ResponseEntity<Long> addSubItem(
		@Parameter(description = "고수 ID", required = true, example = "1")
		Long expertId,

		@Parameter(description = "고수 제공 서비스 추가 요청", required = true)
		@RequestBody ExpertSubItemRequest request) {
		Long id = expertService.addSubItem(expertId, request);

		return ResponseEntity.ok(id);
	}

	@CurrentExpertId
	@DeleteMapping("/sub-items")
	@Operation(summary = "고수 제공 서비스 삭제", description = "고수 계정 내 제공 서비스를 삭제합니다.")
	public ResponseEntity<Object> removeItem(
		@Parameter(description = "고수 ID", required = true, example = "1")
		Long expertId,

		@Parameter(description = "고수 제공 서비스 삭제 요청", required = true)
		@RequestBody ExpertSubItemRequest request) {
		expertService.removeSubItem(expertId, request);

		return ResponseEntity.noContent()
			.build();
	}

	@CurrentExpertId
	@GetMapping("/sub-items")
	@Operation(summary = "고수 제공 서비스 조회", description = "고수 계정 내 제공 서비스를 조회합니다.")
	public ResponseEntity<SubItemsResponse> getSubItemsByExpertId(
		@Parameter(description = "고수 ID", required = true, example = "1")
		Long expertId) {
		SubItemsResponse response = expertService.getSubItemsByExpertId(expertId);

		return ResponseEntity.ok(response);
	}

	@CurrentExpertId
	@PostMapping("/images")
	@Operation(summary = "고수 사진 업로드", description = "고수 계정 내 사진을 업로드합니다.")
	public ResponseEntity<ImagesResponse> uploadImage(
		@Parameter(description = "고수 ID", required = true, example = "1")
		Long expertId,

		@Parameter(description = "고수 사진", required = true)
		MultipartFile file) throws IOException {
		ImageUploadRequest request = new ImageUploadRequest(List.of(file));
		ImagesResponse response = expertService.uploadImage(expertId, request);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@CurrentExpertId
	@DeleteMapping("/images/{filename}")
	@Operation(summary = "고수 사진 삭제", description = "고수 계정 내 사진을 삭제합니다.")
	public ResponseEntity<String> deleteImage(
		@Parameter(description = "고수 ID", required = true, example = "1")
		Long expertId,

		@Parameter(description = "filename", required = true)
		@PathVariable String filename) {
		expertService.deleteImage(expertId, filename);

		return ResponseEntity.ok(null);
	}

	@CurrentExpertId
	@GetMapping("/images")
	@Operation(summary = "고수 사진 전체 조회", description = "고수 계정 내 사진을 모두 조회합니다.")
	public ResponseEntity<ImageResponse> getAllImages(
		@Parameter(description = "고수 ID", required = true, example = "1")
		Long expertId) {
		ImageResponse response = expertService.getAllImages(expertId);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/search")
	@Operation(summary = "고수 찾기", description = "조건에 맞는 고수들을 찾습니다.")
	public ResponseEntity<SlicedExpertsResponse> searchExperts(
		@Parameter(description = "서비스 이름", example = "영어 회화")
		@RequestParam(required = false) String subItem,

		@Parameter(description = "지역", example = "서울특별시 강남구")
		@RequestParam(required = false) String location,

		@Parameter(description = "정렬 방식", example = "rating,desc")
		@PageableDefault(sort = {"reviewCount"}, direction = Sort.Direction.DESC) Pageable pageable) {
		SortType.validateSortColumns(pageable.getSort());

		return ResponseEntity.ok(expertService.findExperts(subItem, location, pageable));
	}
}
