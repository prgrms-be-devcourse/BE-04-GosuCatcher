package com.foo.gosucatcher.domain.expert.application;

import static com.foo.gosucatcher.global.error.ErrorCode.ALREADY_REGISTERED_BY_SUB_ITEM;
import static com.foo.gosucatcher.global.error.ErrorCode.DUPLICATED_EXPERT_STORENAME;
import static com.foo.gosucatcher.global.error.ErrorCode.NOT_FOUND_EXPERT;
import static com.foo.gosucatcher.global.error.ErrorCode.NOT_FOUND_EXPERT_ITEM;
import static com.foo.gosucatcher.global.error.ErrorCode.NOT_FOUND_MEMBER;
import static com.foo.gosucatcher.global.error.ErrorCode.NOT_FOUND_SUB_ITEM;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foo.gosucatcher.domain.expert.application.dto.request.ExpertCreateRequest;
import com.foo.gosucatcher.domain.expert.application.dto.request.ExpertSubItemRequest;
import com.foo.gosucatcher.domain.expert.application.dto.request.ExpertUpdateRequest;
import com.foo.gosucatcher.domain.expert.application.dto.response.ExpertResponse;
import com.foo.gosucatcher.domain.expert.application.dto.response.ExpertsResponse;
import com.foo.gosucatcher.domain.expert.application.dto.response.SlicedExpertsResponse;
import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.expert.domain.ExpertItem;
import com.foo.gosucatcher.domain.expert.domain.ExpertItemRepository;
import com.foo.gosucatcher.domain.expert.domain.ExpertRepository;
import com.foo.gosucatcher.domain.item.application.dto.response.sub.SubItemsResponse;
import com.foo.gosucatcher.domain.item.domain.SubItem;
import com.foo.gosucatcher.domain.item.domain.SubItemRepository;
import com.foo.gosucatcher.domain.member.domain.Member;
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

	public ExpertResponse create(ExpertCreateRequest request, long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MEMBER));

		duplicatedStoreNameCheck(request.storeName());

		Expert newExpert = ExpertCreateRequest.toExpert(member, request);
		expertRepository.save(newExpert);

		return ExpertResponse.from(newExpert);
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
		existingExpert.updateExpert(updatedExpert);

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
		Slice<Expert> expertsSlice = expertRepository.findBySubItemAndLocation(subItem, location, pageable);

		return SlicedExpertsResponse.from(expertsSlice);
	}
}
