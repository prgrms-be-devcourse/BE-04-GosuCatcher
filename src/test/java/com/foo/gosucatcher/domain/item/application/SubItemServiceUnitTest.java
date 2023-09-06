package com.foo.gosucatcher.domain.item.application;

import com.foo.gosucatcher.domain.item.application.dto.request.sub.SubItemCreateRequest;
import com.foo.gosucatcher.domain.item.application.dto.request.sub.SubItemSliceRequest;
import com.foo.gosucatcher.domain.item.application.dto.request.sub.SubItemUpdateRequest;
import com.foo.gosucatcher.domain.item.application.dto.response.sub.SubItemResponse;
import com.foo.gosucatcher.domain.item.application.dto.response.sub.SubItemsResponse;
import com.foo.gosucatcher.domain.item.application.dto.response.sub.SubItemsSliceResponse;
import com.foo.gosucatcher.domain.item.domain.MainItem;
import com.foo.gosucatcher.domain.item.domain.MainItemRepository;
import com.foo.gosucatcher.domain.item.domain.SubItem;
import com.foo.gosucatcher.domain.item.domain.SubItemRepository;
import com.foo.gosucatcher.global.error.exception.BusinessException;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubItemServiceUnitTest {

    @InjectMocks
    private SubItemService subItemService;

    @Mock
    private MainItemRepository mainItemRepository;

    @Mock
    private SubItemRepository subItemRepository;

    private MainItem mainItem;
    private SubItem subItem;
    private SubItemCreateRequest subItemCreateRequest;

    @BeforeEach
    void setUp() {
        mainItem = MainItem.builder()
            .name("청소")
            .description("청소를 구해보세요.")
            .build();

        subItem = SubItem.builder()
            .mainItem(mainItem)
            .name("방 청소")
            .description("방 청소 설명")
            .build();

        subItemCreateRequest = new SubItemCreateRequest(1L, "방 청소", "방 청소 설명");
    }

    @Test
    @DisplayName("하위 서비스 생성,저장 성공")
    void createSubItemSuccessTest() throws Exception {

        //given
        when(subItemRepository.save(any(SubItem.class)))
            .thenReturn(subItem);

        when(subItemRepository.findById(null))
            .thenReturn(Optional.of(subItem));

        when(mainItemRepository.findById(anyLong()))
            .thenReturn(Optional.of(mainItem));

        //when
        SubItemResponse subItemResponse = subItemService.create(subItemCreateRequest);
        SubItem foundSubItem = subItemRepository.findById(subItemResponse.id()).get();

        //then
        assertThat(foundSubItem.getName()).isEqualTo(subItemCreateRequest.name());
        assertThat(foundSubItem.getDescription()).isEqualTo(subItemCreateRequest.description());
        assertThat(foundSubItem.getMainItem().getName()).isEqualTo(mainItem.getName());
    }

    @Test
    @DisplayName("하위 서비스 생성 실패 - 존재하지 않는 메인 서비스")
    void createSubItemFailTest_notFoundMainItem() throws Exception {

        //given
        when(mainItemRepository.findById(anyLong()))
            .thenReturn(Optional.empty());

        //when -> then
        assertThrows(EntityNotFoundException.class,
            () -> subItemService.create(subItemCreateRequest));
    }

    @Test
    @DisplayName("하위 서비스 생성 실패 - 중복된 이름")
    void createSubItemFailTest_DuplicatedName() throws Exception {

        //given
        String duplicateName = "방 청소";

        when(mainItemRepository.findById(anyLong()))
            .thenReturn(Optional.of(mainItem));

        when(subItemRepository.findByName(duplicateName))
            .thenReturn(Optional.of(subItem));

        //when -> then
        assertThrows(BusinessException.class,
            () -> subItemService.create(subItemCreateRequest));
    }

    @Test
    @DisplayName("하위 서비스 전체 조회")
    void findAllSuccessTest() throws Exception {

        //given
        List<SubItem> subItemList = Collections.singletonList(subItem);

        when(subItemRepository.findAll())
            .thenReturn(subItemList);

        //when
        SubItemsResponse subItemsResponse = subItemService.findAll();

        //then
        assertThat(subItemsResponse).isNotNull();
        assertThat(subItemsResponse.subItemsResponse()).hasSize(1);
        assertThat(subItemsResponse.subItemsResponse().get(0).name()).isEqualTo(subItem.getName());
        assertThat(subItemsResponse.subItemsResponse().get(0).description()).isEqualTo(subItem.getDescription());
        assertThat(subItemsResponse.subItemsResponse().get(0).mainItemName()).isEqualTo(mainItem.getName());
    }

    @Test
    @DisplayName("하위 서비스 ID로 조회 성공")
    void findSubItemByIdSuccessTest() throws Exception {

        //given
        Long subItemId = subItem.getId();

        when(subItemRepository.findById(subItemId))
            .thenReturn(Optional.of(subItem));

        //when
        SubItemResponse subItemResponse = subItemService.findById(subItemId);

        //then
        assertThat(subItemResponse.name()).isEqualTo(subItem.getName());
        assertThat(subItemResponse.mainItemName()).isEqualTo(mainItem.getName());
    }

    @Test
    @DisplayName("하위 서비스 ID로 조회 실패 - 존재하지 않는 하위 서비스")
    void findSubItemByIdFailTest_notFoundSubItem() throws Exception {

        //given
        Long subItemId = subItem.getId();
        when(subItemRepository.findById(subItemId))
            .thenReturn(Optional.empty());

        //when -> then
        assertThrows(EntityNotFoundException.class, () -> subItemService.findById(subItemId));
    }

    @Test
    @DisplayName("메인 아이템 이름으로 하위 서비스 검색")
    void findAllByMainItemNameTest() {
        // given
        String mainItemName = "청소";
        int page = 0;
        int size = 10;
        SubItemSliceRequest sliceRequest = new SubItemSliceRequest(page, size);

        List<SubItem> subItems = new ArrayList<>();
        subItems.add(subItem);

        Slice<SubItem> subItemSlice = new SliceImpl<>(subItems, PageRequest.of(page, size), true);

        when(subItemRepository.findAllByMainItemName(mainItemName, PageRequest.of(page, size)))
            .thenReturn(subItemSlice);

        //when
        SubItemsSliceResponse response = subItemService.findAllByMainItemName(mainItemName, sliceRequest);

        //then
        assertThat(response.subItemSlicesResponse()).hasSize(subItems.size());
        assertThat(response.hasNext()).isEqualTo(subItemSlice.hasNext());
    }

    @Test
    @DisplayName("하위 서비스 업데이트 성공")
    void updateSubItemSuccessTest() throws Exception {

        //given
        String newName = "새로운 이름";
        SubItemUpdateRequest subItemUpdateRequest = new SubItemUpdateRequest(newName, "축구 설명");

        when(subItemRepository.findById(null))
            .thenReturn(Optional.of(subItem));

        when(subItemRepository.findByName(newName))
            .thenReturn(Optional.empty());

        //when
        subItemService.update(mainItem.getId(), subItemUpdateRequest);

        //then
        assertThat(subItem.getName()).isEqualTo(newName);
        assertThat(subItem.getDescription()).isEqualTo(subItemUpdateRequest.description());
    }

    @Test
    @DisplayName("하위 서비스 업데이트 실패 - 존재하지 않는 하위 서비스")
    void updateSubItemFailTest_subItemNotFound() throws Exception {

        //given
        String newName = "새로운 이름";
        SubItemUpdateRequest subItemUpdateRequest = new SubItemUpdateRequest(newName, "축구 설명");

        when(subItemRepository.findById(null))
            .thenReturn(Optional.empty());

        //when -> then
        assertThrows(EntityNotFoundException.class,
            () -> subItemService.update(mainItem.getId(), subItemUpdateRequest));
    }

    @Test
    @DisplayName("하위 서비스 삭제 성공")
    void deleteSubItemSuccessTest() throws Exception {

        //given
        when(subItemRepository.findById(null))
            .thenReturn(Optional.of(subItem));

        //when
        assertDoesNotThrow(() -> subItemService.delete(subItem.getId()));

        //then
        verify(subItemRepository, times(1)).delete(subItem);
    }
}
