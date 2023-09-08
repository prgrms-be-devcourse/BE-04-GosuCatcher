package com.foo.gosucatcher.domain.expert.presentation;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
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
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.foo.gosucatcher.domain.expert.application.ExpertService;
import com.foo.gosucatcher.domain.expert.application.dto.request.ExpertCreateRequest;
import com.foo.gosucatcher.domain.expert.application.dto.request.ExpertUpdateRequest;
import com.foo.gosucatcher.domain.expert.application.dto.response.ExpertResponse;
import com.foo.gosucatcher.domain.expert.application.dto.response.ExpertsResponse;
import com.foo.gosucatcher.domain.image.ImageService;
import com.foo.gosucatcher.domain.image.application.dto.request.ImageUploadRequest;
import com.foo.gosucatcher.domain.image.application.dto.response.ImageResponse;
import com.foo.gosucatcher.domain.image.application.dto.response.ImageUploadResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/experts")
public class ExpertController {

	private final ExpertService expertService;
	private final ImageService imageService;

	@PostMapping
	public ResponseEntity<ExpertResponse> create(@Validated @RequestBody ExpertCreateRequest request,
		@RequestParam Long memberId) {
		ExpertResponse expertResponse = expertService.create(request, memberId);

		return ResponseEntity.ok(expertResponse);
	}

	@GetMapping("/{expertId}")
	public ResponseEntity<ExpertResponse> findOne(@PathVariable Long expertId) {
		ExpertResponse expert = expertService.findById(expertId);

		return ResponseEntity.ok(expert);
	}

	@GetMapping
	public ResponseEntity<ExpertsResponse> findAll() {
		ExpertsResponse experts = expertService.findAll();

		return ResponseEntity.ok(experts);
	}

	@PatchMapping("/{expertId}")
	public ResponseEntity<Long> update(@PathVariable Long expertId,
		@Validated @RequestBody ExpertUpdateRequest request) {
		Long updatedExpertId = expertService.update(expertId, request);

		return ResponseEntity.ok(updatedExpertId);
	}

	@DeleteMapping("/{expertId}")
	public ResponseEntity<Void> delete(@PathVariable Long expertId) {
		expertService.delete(expertId);

		return ResponseEntity.ok(null);
	}

	@PostMapping("/{expertId}/images")
	public ResponseEntity<ImageUploadResponse> uploadImage(@PathVariable Long expertId, MultipartFile file) throws
		IllegalStateException,
		IOException {
		ImageUploadRequest request = new ImageUploadRequest(expertId, file);
		String uploadedFilename = imageService.store(request);

		ImageUploadResponse response = new ImageUploadResponse(expertId, uploadedFilename);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(response);
	}

	@GetMapping("/{expertId}/images/{filename}")
	public ResponseEntity<Resource> getImage(@PathVariable Long expertId,
		@PathVariable String filename) {

		Resource file = imageService.loadAsResource(expertId, filename);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
			"attachment; filename=\"" + file.getFilename() + "\"").body(file);
	}

	@DeleteMapping("/{expertId}/images/{filename}")
	public ResponseEntity<String> deleteImage(@PathVariable Long expertId,
		@PathVariable String filename) {
		imageService.delete(expertId, filename);
		return ResponseEntity.ok(null);
	}

	@GetMapping("{expertId}/images")
	public ResponseEntity<List<ImageResponse>> getImages(@PathVariable Long expertId) {
		List<ImageResponse> fileInfos = imageService.loadAll(expertId)
			.map(path -> {
				String filename = path.getFileName().toString();
				String url = MvcUriComponentsBuilder.fromMethodName(ExpertController.class,
					"getImage", expertId, filename).build().toString();
				Long size;
				try {
					size = Files.size(path);
				} catch (IOException e) {
					size = 0L;
				}
				return new ImageResponse(filename, url, size);
			})
			.collect(Collectors.toList());

		return ResponseEntity.ok(fileInfos);
	}
}
