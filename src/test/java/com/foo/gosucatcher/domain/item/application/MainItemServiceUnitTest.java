package com.foo.gosucatcher.domain.item.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.foo.gosucatcher.domain.item.application.dto.request.MainItemCreateRequest;
import com.foo.gosucatcher.domain.item.application.dto.request.MainItemUpdateRequest;
import com.foo.gosucatcher.domain.item.application.dto.response.MainItemResponse;
import com.foo.gosucatcher.domain.item.application.dto.response.MainItemsResponse;
import com.foo.gosucatcher.domain.item.domain.MainItem;
import com.foo.gosucatcher.domain.item.domain.MainItemRepository;
import com.foo.gosucatcher.global.error.exception.BusinessException;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class MainItemServiceUnitTest {

	@InjectMocks
	private MainItemService mainItemService;

	@Mock
	private MainItemRepository mainItemRepository;

	private MainItem mainItem;

	@BeforeEach
	void setUp() {
		mainItem = MainItem.builder()
			.name("알바")
			.description("알바를 구해보세요.")
			.build();
	}

	@Test
	@DisplayName("메인 아이템을 생성,저장한다")
	void createMainItemSuccessTest() throws Exception {

		//given
		MainItemCreateRequest mainItemCreateRequest = new MainItemCreateRequest("알바", "알바를 구해보세요.");

		when(mainItemRepository.save(any(MainItem.class)))
			.thenReturn(mainItem);

		when(mainItemRepository.findById(null))
			.thenReturn(Optional.of(mainItem));

		//when
		MainItemResponse mainItemResponse = mainItemService.create(mainItemCreateRequest);
		MainItemResponse foundMainItem = mainItemService.findById(mainItemResponse.id());

		//then
		assertThat(foundMainItem.name()).isEqualTo(mainItemCreateRequest.name());
		assertThat(foundMainItem.description()).isEqualTo(mainItemCreateRequest.description());
	}

	@Test
	@DisplayName("메인 아이템 생성,저장 실패 -> 중복된 이름")
	void createMainItemFailTest_duplicatedName() throws Exception {

		//given
		String duplicateName = "알바";
		MainItemCreateRequest mainItemCreateRequest = new MainItemCreateRequest(duplicateName, "알바를 구하세요.");

		//when
		when(mainItemRepository.findByName(duplicateName)).thenReturn(Optional.of(mainItem));

		//then
		assertThrows(BusinessException.class, () -> mainItemService.create(mainItemCreateRequest));
	}

	@Test
	@DisplayName("메인 아이템을 ID로 조회한다")
	void findByMainItemWithIdSuccessTest() throws Exception {

		//given
		Long mainItemId = 1L;

		when(mainItemRepository.findById(mainItemId))
			.thenReturn(Optional.of(mainItem));

		//when
		MainItemResponse foundMainItem = mainItemService.findById(mainItemId);

		//then
		assertThat(foundMainItem).isNotNull();
		assertThat(foundMainItem.name()).isEqualTo(mainItem.getName());
		assertThat(foundMainItem.description()).isEqualTo(mainItem.getDescription());
	}

	@Test
	@DisplayName("메인 아이템을 ID로 조회 실패 -> 존재하지 않는 메인 아이템")
	void findByMainItemWithIdFailTest_notFoundMainItem() throws Exception {

		//given
		Long mainItemId = 1L;

		when(mainItemRepository.findById(mainItemId))
			.thenReturn(Optional.empty());

		//when -> then
		assertThrows(EntityNotFoundException.class, () -> mainItemService.findById(mainItemId));
	}

	@Test
	@DisplayName("저장된 메인 아이템을 전체 조회한다")
	void findAllMainItemSuccessTest() throws Exception {

		//given
		List<MainItem> mainItemList = Collections.singletonList(mainItem);

		when(mainItemRepository.findAll())
			.thenReturn(mainItemList);

		//when
		MainItemsResponse mainItemsResponse = mainItemService.findAll();

		//then
		assertThat(mainItemsResponse).isNotNull();
		assertThat(mainItemsResponse.mainItemsResponse()).hasSize(1);
		assertThat(mainItemsResponse.mainItemsResponse().get(0).name()).isEqualTo(mainItem.getName());
		assertThat(mainItemsResponse.mainItemsResponse().get(0).description()).isEqualTo(mainItem.getDescription());
	}

	@Test
	@DisplayName("메인 아이템 전체 조회 실패 -> 저장된 메인 아이템이 없다")
	void findAllEmptyListTest() {

		//given
		List<MainItem> mainItemList = Collections.emptyList();

		when(mainItemRepository.findAll())
			.thenReturn(mainItemList);

		//when
		MainItemsResponse mainItemsResponse = mainItemService.findAll();

		//then
		assertThat(mainItemsResponse).isNotNull();
		assertThat(mainItemsResponse.mainItemsResponse()).isEmpty();
	}

	@Test
	@DisplayName("메인 아이템 업데이트 - 성공")
	void updateMainItemSuccessTest() {

		//given
		Long itemId = 1L;
		String newName = "새로운 이름";
		MainItemUpdateRequest request = new MainItemUpdateRequest(newName, "레슨을 받으세요.");

		when(mainItemRepository.findById(itemId)).thenReturn(Optional.of(mainItem));

		when(mainItemRepository.findByName(newName)).thenReturn(Optional.empty());

		//when
		mainItemService.update(1L, request);

		//then
		assertThat(mainItem.getName()).isEqualTo(newName);
	}

	@Test
	@DisplayName("메인 아이템 업데이트 - 실패 (존재하지 않는 아이템)")
	void updateMainItemFailTest_ItemNotFound() {

		//given
		Long itemId = 1L;
		String newName = "새로운 이름";
		MainItemUpdateRequest request = new MainItemUpdateRequest(newName, "새로운 설명");

		when(mainItemRepository.findById(itemId)).thenReturn(Optional.empty());

		//when -> then
		assertThrows(EntityNotFoundException.class, () -> mainItemService.update(itemId, request));
	}

	@Test
	@DisplayName("메인 아이템 업데이트 - 실패 (중복된 이름)")
	void updateMainItemFailureTest_DuplicateName() {

		//given
		Long itemId = 1L;
		String newName = "알바";
		MainItemUpdateRequest request = new MainItemUpdateRequest(newName, "새로운 설명");

		when(mainItemRepository.findById(itemId)).thenReturn(Optional.of(mainItem));
		when(mainItemRepository.findByName(newName)).thenReturn(Optional.of(mainItem));

		//when -> then
		assertThrows(BusinessException.class, () -> mainItemService.update(itemId, request));
	}

	@Test
	@DisplayName("메인 아이템 삭제")
	void deleteMainItemSuccessTest() throws Exception {

		//given
		Long mainItemId = 1L;
		when(mainItemRepository.findById(mainItemId)).thenReturn(Optional.of(mainItem));

		//when
		assertDoesNotThrow(() -> mainItemService.delete(mainItemId));

		//then
		verify(mainItemRepository, times(1)).delete(mainItem);
	}

	@Test
	@DisplayName("메인 아이템 삭제 - 실패 (아이템을 찾을 수 없는 경우)")
	void deleteMainItemFailTest_ItemNotFound() {

		// given
		Long itemId = 1L;

		when(mainItemRepository.findById(itemId)).thenReturn(Optional.empty());

		// when -> then
		assertThrows(EntityNotFoundException.class, () -> mainItemService.delete(itemId));
	}

	@Test
	@DisplayName("모든 메인 아이템 삭제 - 성공")
	void deleteAllMainItemsSuccessTest() {

		// given
		doNothing().when(mainItemRepository).deleteAll();

		//when
		mainItemService.deleteAll();

		//then
		verify(mainItemRepository, times(1)).deleteAll();
		List<MainItem> mainItems = mainItemRepository.findAll();
		assertTrue(mainItems.isEmpty());
	}
}