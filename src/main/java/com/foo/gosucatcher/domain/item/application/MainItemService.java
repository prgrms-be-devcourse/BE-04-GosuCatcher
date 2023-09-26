package com.foo.gosucatcher.domain.item.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foo.gosucatcher.domain.item.application.dto.request.main.MainItemCreateRequest;
import com.foo.gosucatcher.domain.item.application.dto.request.main.MainItemUpdateRequest;
import com.foo.gosucatcher.domain.item.application.dto.response.main.MainItemResponse;
import com.foo.gosucatcher.domain.item.application.dto.response.main.MainItemsResponse;
import com.foo.gosucatcher.domain.item.domain.MainItem;
import com.foo.gosucatcher.domain.item.domain.MainItemRepository;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.BusinessException;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MainItemService {

    private final MainItemRepository mainItemRepository;

    public MainItemResponse create(MainItemCreateRequest request) {
        duplicatedNameCheck(request.name());

        MainItem mainItem = MainItemCreateRequest.toMainItem(request);

        MainItem savedMainItem = mainItemRepository.save(mainItem);

        return MainItemResponse.from(savedMainItem);
    }

    @Transactional(readOnly = true)
    public MainItemResponse findById(Long id) {

        MainItem mainItem = mainItemRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MAIN_ITEM));

        return MainItemResponse.from(mainItem);
    }

    @Transactional(readOnly = true)
    public MainItemsResponse findAll() {
        List<MainItem> mainItemList = mainItemRepository.findAll();

        return MainItemsResponse.from(mainItemList);
    }

    public MainItemResponse update(Long id, MainItemUpdateRequest request) {
        MainItem foundMainItem = mainItemRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MAIN_ITEM));

        duplicatedNameCheck(request.name());

        MainItem mainItem = MainItemUpdateRequest.toMainItem(request);
        foundMainItem.update(mainItem);

        return MainItemResponse.from(foundMainItem);
    }

    public void delete(Long id) {
        MainItem mainItem = mainItemRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MAIN_ITEM));

        mainItemRepository.delete(mainItem);
    }

    private void duplicatedNameCheck(String name) {
        mainItemRepository.findByName(name).ifPresent(mainItem -> {
            throw new BusinessException(ErrorCode.DUPLICATED_MAIN_ITEM_NAME);
        });
    }
}
