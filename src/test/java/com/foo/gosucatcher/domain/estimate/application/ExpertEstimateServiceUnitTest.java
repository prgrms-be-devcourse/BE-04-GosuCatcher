package com.foo.gosucatcher.domain.estimate.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.foo.gosucatcher.domain.estimate.application.dto.request.ExpertEstimateCreateRequest;
import com.foo.gosucatcher.domain.estimate.application.dto.request.ExpertEstimateUpdateRequest;
import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertEstimateResponse;
import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertEstimatesResponse;
import com.foo.gosucatcher.domain.estimate.domain.ExpertEstimate;
import com.foo.gosucatcher.domain.estimate.domain.ExpertEstimateRepository;
import com.foo.gosucatcher.domain.estimate.domain.MemberEstimate;
import com.foo.gosucatcher.domain.estimate.domain.MemberEstimateRepository;
import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.expert.domain.ExpertRepository;
import com.foo.gosucatcher.domain.item.domain.MainItem;
import com.foo.gosucatcher.domain.item.domain.SubItem;
import com.foo.gosucatcher.domain.item.domain.SubItemRepository;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class ExpertEstimateServiceUnitTest {

	@InjectMocks
	private ExpertEstimateService expertEstimateService;

	@Mock
	private ExpertEstimateRepository expertEstimateRepository;

	@Mock
	private ExpertRepository expertRepository;

	@Mock
	private MemberEstimateRepository memberEstimateRepository;

	@Mock
	private SubItemRepository subItemRepository;

	private Expert expert;
	private Member member;
	private MainItem mainItem;
	private SubItem subItem;
	private MemberEstimate memberEstimate;
	private ExpertEstimate expertEstimate;

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

		memberEstimate = MemberEstimate.builder()
			.member(member)
			.subItem(subItem)
			.preferredStartDate(LocalDateTime.now().plusDays(1))
			.detailedDescription("메시가 되고 싶어요")
			.build();

		expertEstimate = ExpertEstimate.builder()
			.totalCost(10000)
			.expert(expert)
			.memberEstimate(memberEstimate)
			.description("메시를 만들어 드립니다")
			.build();
	}

	@Test
	@DisplayName("고수 응답 견적서 생성 성공")
	void createExpertEstimateSuccessTest() throws Exception {

		//given
		Long expertId = 1L;
		ExpertEstimateCreateRequest request =
			new ExpertEstimateCreateRequest(memberEstimate.getId(), subItem.getId(), 100, "메시를 만들어 드립니다.", true);

		when(expertEstimateRepository.save(any(ExpertEstimate.class)))
			.thenReturn(expertEstimate);
		when(expertRepository.findById(expertId))
			.thenReturn(Optional.of(expert));
		when(memberEstimateRepository.findById(memberEstimate.getId()))
			.thenReturn(Optional.of(memberEstimate));
		when(subItemRepository.findById(subItem.getId()))
			.thenReturn(Optional.of(subItem));

		//when
		ExpertEstimateResponse expertEstimateResponse = expertEstimateService.create(expertId,
			request);

		//then
		assertThat(expertEstimateResponse.totalCost()).isEqualTo(request.totalCost());
		assertThat(expertEstimateResponse.memberEstimateId()).isEqualTo(memberEstimate.getId());
	}

	@Test
	@DisplayName("고수 견적서 생성 실패 - 존재하지 않는 고수")
	void createExpertEstimateFailTest_notFoundExpert() throws Exception {

		//given
		ExpertEstimateCreateRequest request =
			new ExpertEstimateCreateRequest(memberEstimate.getId(), subItem.getId(), 100, "메시를 만들어 드립니다.", true);

		when(expertRepository.findById(anyLong()))
			.thenReturn(Optional.empty());

		//when -> then
		assertThrows(EntityNotFoundException.class,
			() -> expertEstimateService.create(1L, request));
	}

	@Test
	@DisplayName("고수 견적서 생성 실패 - 존재하지 않는 고객의 요청서")
	void createExpertEstimateFailTest_notFoundMemberEstimate() throws Exception {

		//given
		ExpertEstimateCreateRequest request =
			new ExpertEstimateCreateRequest(999L, subItem.getId(), 100, "메시를 만들어 드립니다.", true);

		when(expertRepository.findById(anyLong()))
			.thenReturn(Optional.of(expert));
		when(memberEstimateRepository.findById(request.memberEstimateId()))
			.thenReturn(Optional.empty());

		//when -> then
		assertThrows(EntityNotFoundException.class,
			() -> expertEstimateService.create(1L, request));
	}

	@Test
	@DisplayName("고수 견적서 전체 조회 성공")
	void findAllSuccessTest() throws Exception {

		//given
		List<ExpertEstimate> estimates = Arrays.asList(expertEstimate);

		when(expertEstimateRepository.findAll())
			.thenReturn(estimates);

		//when
		ExpertEstimatesResponse estimatesResponse = expertEstimateService.findAll();

		//then
		assertThat(estimatesResponse.expertResponseEstimatesResponse()).hasSize(1);
		assertThat(estimatesResponse.expertResponseEstimatesResponse().get(0).expertId()).isEqualTo(expert.getId());
		assertThat(estimatesResponse.expertResponseEstimatesResponse().get(0).totalCost()).isEqualTo(
			expertEstimate.getTotalCost());
	}

	@Test
	@DisplayName("ID로 고수 견적서 조회 성공")
	void findExpertEstimateByIdSuccessTest() throws Exception {

		//given
		Long expertEstimateId = expertEstimate.getId();
		when(expertEstimateRepository.findById(expertEstimateId))
			.thenReturn(Optional.of(expertEstimate));

		//when
		ExpertEstimateResponse estimateResponse = expertEstimateService.findById(expertEstimateId);

		//then
		assertThat(estimateResponse.id()).isEqualTo(expertEstimate.getId());
		assertThat(estimateResponse.totalCost()).isEqualTo(expertEstimate.getTotalCost());
		assertThat(estimateResponse.expertId()).isEqualTo(expertEstimate.getExpert().getId());
		assertThat(estimateResponse.memberEstimateId()).isEqualTo(
			expertEstimate.getMemberEstimate().getId());
	}

	@Test
	@DisplayName("ID로 고수 견적서 조회 실패 - 존재하지 않는 견적서")
	void findExpertEstimateByIdFialTest_notFoundExpertEstimate() throws Exception {

		//given
		Long expertEstimateId = expertEstimate.getId();
		when(expertEstimateRepository.findById(expertEstimateId))
			.thenReturn(Optional.empty());

		//when -> then
		assertThrows(EntityNotFoundException.class,
			() -> expertEstimateService.findById(expertEstimateId));
	}

	@Test
	@DisplayName("고수 견적서 수정 성공")
	void updateExpertEstimateSuccessTest() throws Exception {

		//given
		int newTotalCost = 333;
		String newDescription = "호날두를 만들어 드립니다.";
		ExpertEstimateUpdateRequest updateRequest = new ExpertEstimateUpdateRequest(
			newTotalCost, newDescription, true);

		when(expertEstimateRepository.findById(null))
			.thenReturn(Optional.of(expertEstimate));

		//when
		expertEstimateService.update(expertEstimate.getId(), updateRequest);

		//then
		assertThat(expertEstimate.getTotalCost()).isEqualTo(newTotalCost);
		assertThat(expertEstimate.getDescription()).isEqualTo(newDescription);
	}

	@Test
	@DisplayName("고수 견적서 수정 실패 - 존재하지 않는 고수 견적서")
	void updateExpertEstimateFailTest_notFoundExpertEstimate() throws Exception {

		//given
		int newTotalCost = 333;
		String newDescription = "호날두를 만들어 드립니다.";
		ExpertEstimateUpdateRequest updateRequest = new ExpertEstimateUpdateRequest(
			newTotalCost, newDescription, true);

		when(expertEstimateRepository.findById(null))
			.thenReturn(Optional.empty());

		//when -> then
		assertThrows(EntityNotFoundException.class,
			() -> expertEstimateService.update(expertEstimate.getId(), updateRequest));
	}

	@Test
	@DisplayName("고수 견적서 삭제 성공")
	void deleteExpertEstimateSuccessTest() throws Exception {

		//given
		when(expertEstimateRepository.findById(null))
			.thenReturn(Optional.of(expertEstimate));

		//when
		assertDoesNotThrow(() -> expertEstimateService.delete(expertEstimate.getId()));

		//then
		verify(expertEstimateRepository, times(1)).delete(expertEstimate);
	}
}
