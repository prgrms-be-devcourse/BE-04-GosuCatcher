package com.foo.gosucatcher.domain.item.application;

import com.foo.gosucatcher.domain.item.application.dto.request.sub.SubItemCreateRequest;
import com.foo.gosucatcher.domain.item.application.dto.request.sub.SubItemUpdateRequest;
import com.foo.gosucatcher.domain.item.application.dto.response.sub.SubItemResponse;
import com.foo.gosucatcher.domain.item.application.dto.response.sub.SubItemSliceResponse;
import com.foo.gosucatcher.domain.item.application.dto.response.sub.SubItemsResponse;
import com.foo.gosucatcher.domain.item.application.dto.response.sub.SubItemsSliceResponse;
import com.foo.gosucatcher.domain.item.domain.MainItem;
import com.foo.gosucatcher.domain.item.domain.MainItemRepository;
import com.foo.gosucatcher.domain.item.domain.SubItem;
import com.foo.gosucatcher.domain.item.domain.SubItemRepository;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.BusinessException;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SubItemService {

    private final SubItemRepository subItemRepository;
    private final MainItemRepository mainItemRepository;

    public SubItemResponse create(SubItemCreateRequest request) {
        MainItem mainItem = mainItemRepository.findById(request.mainItemId())
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

    @Transactional(readOnly = true)
    public SubItemsSliceResponse findAllByMainItemName(String mainItemName, Pageable pageable) {
        Slice<SubItem> subItems = subItemRepository.findAllByMainItemName(mainItemName, pageable);

        return SubItemsSliceResponse.of(subItems.stream()
            .map(SubItemSliceResponse::from)
            .toList(), subItems.hasNext());
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
