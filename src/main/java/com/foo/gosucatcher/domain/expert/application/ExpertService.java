package com.foo.gosucatcher.domain.expert.application;

import static com.foo.gosucatcher.global.error.ErrorCode.ALREADY_REGISTERED_BY_SUB_ITEM;
import static com.foo.gosucatcher.global.error.ErrorCode.DUPLICATED_EXPERT_STORENAME;
import static com.foo.gosucatcher.global.error.ErrorCode.NOT_FOUND_EXPERT;
import static com.foo.gosucatcher.global.error.ErrorCode.NOT_FOUND_EXPERT_ITEM;
import static com.foo.gosucatcher.global.error.ErrorCode.NOT_FOUND_SUB_ITEM;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foo.gosucatcher.domain.expert.application.dto.request.ExpertSubItemRequest;
import com.foo.gosucatcher.domain.expert.application.dto.request.ExpertUpdateRequest;
import com.foo.gosucatcher.domain.expert.application.dto.response.ExpertResponse;
import com.foo.gosucatcher.domain.expert.application.dto.response.ExpertsResponse;
import com.foo.gosucatcher.domain.expert.application.dto.response.SlicedExpertsResponse;
import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.expert.domain.ExpertImage;
import com.foo.gosucatcher.domain.expert.domain.ExpertImageRepository;
import com.foo.gosucatcher.domain.expert.domain.ExpertItem;
import com.foo.gosucatcher.domain.expert.domain.ExpertItemRepository;
import com.foo.gosucatcher.domain.expert.domain.ExpertRepository;
import com.foo.gosucatcher.domain.image.ImageService;
import com.foo.gosucatcher.domain.image.application.dto.request.ImageDeleteRequest;
import com.foo.gosucatcher.domain.image.application.dto.request.ImageUploadRequest;
import com.foo.gosucatcher.domain.image.application.dto.response.ImageResponse;
import com.foo.gosucatcher.domain.image.application.dto.response.ImagesResponse;
import com.foo.gosucatcher.domain.item.application.dto.response.sub.SubItemsResponse;
import com.foo.gosucatcher.domain.item.domain.SubItem;
import com.foo.gosucatcher.domain.item.domain.SubItemRepository;
import com.foo.gosucatcher.domain.member.application.MemberProfileService;
import com.foo.gosucatcher.domain.member.domain.MemberRepository;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.BusinessException;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ExpertService {

	private final ExpertRepository expertRepository;
	private final MemberRepository memberRepository;
	private final SubItemRepository subItemRepository;
	private final ExpertItemRepository expertItemRepository;
	private final ImageService imageService;
	private final ExpertImageRepository expertImageRepository;
	private final MemberProfileService memberProfileService;

	public ExpertResponse create(long expertId, ExpertUpdateRequest request) {
		Expert existingExpert = expertRepository.findById(expertId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_EXPERT));

		duplicatedStoreNameCheck(request.storeName());

		existingExpert.update(ExpertUpdateRequest.toExpert(request));

		return ExpertResponse.from(existingExpert);
	}

	@Transactional(readOnly = true)
	public ExpertResponse findById(Long id) {
		Expert expert = expertRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_EXPERT));

		return ExpertResponse.from(expert);
	}

	@Transactional(readOnly = true)
	public ExpertsResponse findAll() {
		List<Expert> experts = expertRepository.findAll();
		ExpertsResponse expertsResponse = ExpertsResponse.from(experts);

		return expertsResponse;
	}

	public Long update(Long id, ExpertUpdateRequest request) {
		Expert existingExpert = expertRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_EXPERT));

		duplicatedStoreNameCheck(request.storeName());

		Expert updatedExpert = ExpertUpdateRequest.toExpert(request);
		existingExpert.update(updatedExpert);

		return existingExpert.getId();
	}

	public void delete(Long id) {
		Expert expert = expertRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_EXPERT));

		expertRepository.delete(expert);
	}

	public Long addSubItem(Long id, ExpertSubItemRequest addSubItemRequest) {
		Expert expert = expertRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_EXPERT));

		SubItem subItem = subItemRepository.findByName(addSubItemRequest.subItemName())
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_SUB_ITEM));

		checkAlreadyRegisteredSubItem(expert, subItem);

		ExpertItem expertItem = ExpertItem.builder()
			.expert(expert)
			.subItem(subItem)
			.build();

		expert.addExpertItem(expertItem);

		expertRepository.save(expert);

		return expert.getId();
	}

	public void removeSubItem(Long id, ExpertSubItemRequest removeSubItemRequest) {
		Expert expert = expertRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_EXPERT));

		SubItem subItem = subItemRepository.findByName(removeSubItemRequest.subItemName())
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_SUB_ITEM));

		ExpertItem expertItem = expertItemRepository.findByExpertAndSubItem(expert, subItem)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_EXPERT_ITEM));

		expert.removeExpertItem(expertItem);

		expertItemRepository.delete(expertItem);
	}

	@Transactional(readOnly = true)
	public SubItemsResponse getSubItemsByExpertId(Long id) {
		Expert foundExpert = expertRepository.findExpertWithSubItemsById(id)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_SUB_ITEM));

		return SubItemsResponse.from(foundExpert);
	}

	private void checkAlreadyRegisteredSubItem(Expert expert, SubItem subItem) {
		List<ExpertItem> expertItemList = expert.getExpertItemList();

		expertItemList.stream()
			.filter(expertItem -> {
				Long registeredSubItemId = expertItem.getSubItem().getId();
				Long requestedSubItemId = subItem.getId();
				return registeredSubItemId.equals(requestedSubItemId);
			})
			.forEach(expertItem -> {
				throw new BusinessException(ALREADY_REGISTERED_BY_SUB_ITEM);
			});
	}

	private void duplicatedStoreNameCheck(String storeName) {
		Optional<Expert> existingExpert = expertRepository.findByStoreName(storeName);
		if (existingExpert.isPresent()) {
			throw new BusinessException(DUPLICATED_EXPERT_STORENAME);
		}
	}

	@Transactional(readOnly = true)
	public SlicedExpertsResponse findExperts(String subItem, String location, Pageable pageable) {
		Slice<Long> expertIdsSlice = expertRepository.findExpertIdsBySubItemAndLocation(subItem, location, pageable);

		List<Long> expertIds = expertIdsSlice.getContent();

		List<Object[]> results = expertRepository.findExpertsWithProfileImageByIds(expertIds);

		List<ExpertResponse> expertResponses = results.stream()
			.map(result -> {
				Expert expert = (Expert)result[0];
				String filename = (String)result[1];

				return new ExpertResponse(
					expert.getId(),
					expert.getStoreName(),
					expert.getLocation(),
					expert.getMaxTravelDistance(),
					expert.getDescription(),
					expert.getRating(),
					expert.getReviewCount(),
					filename
				);
			})
			.toList();

		return new SlicedExpertsResponse(expertResponses, expertIdsSlice.hasNext());
	}

	public ImagesResponse uploadImage(Long expertId, ImageUploadRequest request) {

		ImagesResponse response = imageService.store(request);

		Expert expert = expertRepository.findById(expertId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_EXPERT));

		for (String filename : response.filenames()) {

			ExpertImage expertImage = ExpertImage.builder()
				.filename(filename)
				.expert(expert)
				.build();

			expertImageRepository.save(expertImage);
		}

		return response;
	}

	@Transactional(readOnly = true)
	public ImageResponse getAllImages(Long expertId) {

		Expert expert = expertRepository.findById(expertId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_EXPERT));

		List<ExpertImage> expertImages = expertImageRepository.findAllByExpert(expert);
		List<String> filenames = expertImages.stream()
			.map(ExpertImage::getFilename)
			.collect(Collectors.toList());

		return new ImageResponse(filenames);
	}

	public void deleteImage(Long expertId, String filename) {

		Expert expert = expertRepository.findById(expertId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_EXPERT));

		ExpertImage expertImage = expertImageRepository.findByFilenameAndExpert(filename, expert)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_IMAGE));

		imageService.delete(new ImageDeleteRequest(List.of(filename)));

		expertImageRepository.delete(expertImage);
	}
}
