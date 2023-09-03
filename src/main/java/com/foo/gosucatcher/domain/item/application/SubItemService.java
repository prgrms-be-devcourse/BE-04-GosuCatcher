package com.foo.gosucatcher.domain.item.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foo.gosucatcher.domain.item.application.dto.request.SubItemCreateRequest;
import com.foo.gosucatcher.domain.item.application.dto.request.SubItemUpdateRequest;
import com.foo.gosucatcher.domain.item.application.dto.response.SubItemResponse;
import com.foo.gosucatcher.domain.item.application.dto.response.SubItemsResponse;
import com.foo.gosucatcher.domain.item.domain.MainItem;
import com.foo.gosucatcher.domain.item.domain.MainItemRepository;
import com.foo.gosucatcher.domain.item.domain.SubItem;
import com.foo.gosucatcher.domain.item.domain.SubItemRepository;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.BusinessException;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SubItemService {

	private final SubItemRepository subItemRepository;
	private final MainItemRepository mainItemRepository;

	public SubItemResponse create(Long mainItemId, SubItemCreateRequest request) {
		MainItem mainItem = mainItemRepository.findById(mainItemId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MAIN_ITEM));

		duplicatedNameCheck(request.name());

		SubItem subItem = SubItemCreateRequest.toSubItem(mainItem, request);
		subItemRepository.save(subItem);

		return SubItemResponse.from(subItem);
	}

	@Transactional(readOnly = true)
	public SubItemResponse findById(Long id) {
		SubItem subItem = subItemRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_SUB_ITEM));

		return SubItemResponse.from(subItem);
	}

	@Transactional(readOnly = true)
	public SubItemsResponse findAll() {
		List<SubItem> subItemList = subItemRepository.findAll();

		return SubItemsResponse.from(subItemList);
	}

	public Long update(Long id, SubItemUpdateRequest request) {
		SubItem foundSubItem = subItemRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_SUB_ITEM));

		duplicatedNameCheck(request.name());

		SubItem subItem = SubItemUpdateRequest.toSubItem(foundSubItem.getMainItem(), request);

		foundSubItem.update(subItem);

		return foundSubItem.getId();
	}

	public void delete(Long id) {
		SubItem subItem = subItemRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_SUB_ITEM));

		subItemRepository.delete(subItem);
	}

	private void duplicatedNameCheck(String name) {
		subItemRepository.findByName(name).ifPresent(subItem -> {
			throw new BusinessException(ErrorCode.DUPLICATED_SUB_ITEM_NAME);
		});
	}
}