package com.foo.gosucatcher.domain.expert.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.foo.gosucatcher.domain.expert.application.dto.request.ExpertSubItemRequest;
import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.expert.domain.ExpertItem;
import com.foo.gosucatcher.domain.expert.domain.ExpertItemRepository;
import com.foo.gosucatcher.domain.expert.domain.ExpertRepository;
import com.foo.gosucatcher.domain.item.application.dto.response.sub.SubItemResponse;
import com.foo.gosucatcher.domain.item.application.dto.response.sub.SubItemsResponse;
import com.foo.gosucatcher.domain.item.domain.MainItem;
import com.foo.gosucatcher.domain.item.domain.SubItem;
import com.foo.gosucatcher.domain.item.domain.SubItemRepository;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class ExpertServiceTest {

	@InjectMocks
	private ExpertService expertService;

	@Mock
	private ExpertRepository expertRepository;

	@Mock
	private SubItemRepository subItemRepository;

	@Mock
	private ExpertItemRepository expertItemRepository;

	private Expert expert;
	private Member member;
	private MainItem mainItem;
	private SubItem subItem;
	private ExpertItem expertItem;

	@BeforeEach
	void setUp() {
		member = Member.builder()
			.name("이홍섭")
			.password("q1w2e3")
			.email("sjun@naver.com")
			.phoneNumber("010")
			.build();

		mainItem = MainItem.builder()
			.name("메인 서비스 이름")
			.description("메인 서비스 설명").
			build();

		subItem = SubItem.builder()
			.mainItem(mainItem)
			.name("세부 서비스 이름")
			.description("세부 서비스 설명")
			.build();

		expert = Expert.builder()
			.member(member)
			.storeName("축구 레슨")
			.location("서울시 강남구")
			.maxTravelDistance(10)
			.description("축구 레슨 해드립니다.")
			.build();

		expertItem = ExpertItem.builder()
			.expert(expert)
			.subItem(subItem)
			.build();
	}

	@Test
	@DisplayName("고수가 제공할 서비스 등록 성공")
	void addSubItemSuccessTest() throws Exception {
		//given
		ExpertSubItemRequest request = new ExpertSubItemRequest("세부 서비스 이름");

		when(expertRepository.findById(expert.getId()))
			.thenReturn(Optional.of(expert));
		when(subItemRepository.findByName(request.subItemName()))
			.thenReturn(Optional.of(subItem));

		//when
		Long addedExpertId = expertService.addSubItem(expert.getId(), request);

		//then
		assertThat(addedExpertId).isEqualTo(expert.getId());
		assertThat(expert.getExpertItemList()).isNotNull();
		assertThat(expert.getExpertItemList().get(0).getSubItem().getName()).isEqualTo("세부 서비스 이름");
	}

	@DisplayName("고수가 제공할 서비스 등록 실패 - 존재하지 않는 고수")
	@Test
	void addSubItemFailTest_ExpertNotFound() {
		// given
		Long nonExistentExpertId = 999L;
		ExpertSubItemRequest request = new ExpertSubItemRequest("세부 서비스 이름");

		Mockito.when(expertRepository.findById(nonExistentExpertId))
			.thenReturn(Optional.empty());

		// when and then
		assertThrows(EntityNotFoundException.class, () -> {
			expertService.addSubItem(nonExistentExpertId, request);
		});
	}

	@DisplayName("고수가 제공할 서비스 등록 실패 - 존재하지 않는 세부 서비스")
	@Test
	void addSubItemFailTest_SubItemNotFound() {
		// given
		Long expertId = 1L;
		ExpertSubItemRequest request = new ExpertSubItemRequest("세부 서비스 이름");

		Mockito.when(expertRepository.findById(expertId)).thenReturn(Optional.of(expert));
		Mockito.when(subItemRepository.findByName(request.subItemName())).thenReturn(Optional.empty());

		// when and then
		assertThrows(EntityNotFoundException.class, () -> {
			expertService.addSubItem(expertId, request);
		});
	}

	@Test
	@DisplayName("고수가 제공중인 서비스 삭제 성공")
	void remove() throws Exception {

		//given
		ExpertSubItemRequest removeSubItemRequest = new ExpertSubItemRequest("세부 서비스 이름");

		when(expertRepository.findById(expert.getId()))
			.thenReturn(Optional.of(expert));
		when(subItemRepository.findByName("세부 서비스 이름")).
			thenReturn(Optional.of(subItem));
		when(expertItemRepository.findByExpertAndSubItem(expert, subItem))
			.thenReturn(Optional.of(expertItem));

		//when
		expertService.removeSubItem(expert.getId(), removeSubItemRequest);

		//then
		assertFalse(expert.getExpertItemList().contains(expertItem));
		assertThat(expert.getExpertItemList()).isEmpty();
		verify(expertItemRepository, times(1)).delete(expertItem);
	}

	@Test
	@DisplayName("고수가 등록하지 않은 서비스를 삭제시 실패")
	void removeSubItemFailTest_notFoundExpertItem() throws Exception {

		//given
		ExpertSubItemRequest removeSubItemRequest = new ExpertSubItemRequest("세부 서비스 이름");

		when(expertRepository.findById(expert.getId()))
			.thenReturn(Optional.of(expert));
		when(subItemRepository.findByName("세부 서비스 이름")).
			thenReturn(Optional.of(subItem));
		when(expertItemRepository.findByExpertAndSubItem(expert, subItem))
			.thenReturn(Optional.empty());

		//when -> then
		assertThrows(EntityNotFoundException.class,
			() -> expertService.removeSubItem(expert.getId(), removeSubItemRequest));
	}

	@Test
	@DisplayName("고수 서브 아이템 조회 성공")
	void findAllByExpertIdSuccessTest() {
		// given
		when(expertRepository.findExpertWithSubItemsById(1L))
			.thenReturn(Optional.of(expert));

		expert.addExpertItem(expertItem);

		//when
		SubItemsResponse subItemsResponse = expertService.getSubItemsByExpertId(1L);

		//then
		List<SubItemResponse> subItemResponses = subItemsResponse.subItemsResponse();
		assertThat(subItemResponses).hasSize(1);
		assertThat(subItemResponses.get(0).name()).isEqualTo(subItem.getName());
		assertThat(subItemResponses.get(0).description()).isEqualTo(subItem.getDescription());
	}

	@Test
	@DisplayName("고수 서브 아이템 조회 실패 - 고수가 없는 경우")
	void findAllByExpertIdFailTest_notFoundExpert() {
		//given
		when(expertRepository.findExpertWithSubItemsById(1L))
			.thenReturn(Optional.empty());

		//when -> then
		assertThrows(EntityNotFoundException.class, () -> {
			expertService.getSubItemsByExpertId(1L);
		});
	}
}
