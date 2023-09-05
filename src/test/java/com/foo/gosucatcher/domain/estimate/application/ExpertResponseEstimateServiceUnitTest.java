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

import com.foo.gosucatcher.domain.estimate.application.dto.request.ExpertResponseEstimateCreateRequest;
import com.foo.gosucatcher.domain.estimate.application.dto.request.ExpertResponseEstimateUpdateRequest;
import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertResponseEstimateResponse;
import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertResponseEstimatesResponse;
import com.foo.gosucatcher.domain.estimate.domain.ExpertResponseEstimate;
import com.foo.gosucatcher.domain.estimate.domain.ExpertResponseEstimateRepository;
import com.foo.gosucatcher.domain.estimate.domain.MemberRequestEstimate;
import com.foo.gosucatcher.domain.estimate.domain.MemberRequestEstimateRepository;
import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.expert.domain.ExpertRepository;
import com.foo.gosucatcher.domain.item.domain.MainItem;
import com.foo.gosucatcher.domain.item.domain.SubItem;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class ExpertResponseEstimateServiceUnitTest {

	@InjectMocks
	private ExpertResponseEstimateService expertResponseEstimateService;

	@Mock
	private ExpertResponseEstimateRepository expertResponseEstimateRepository;

	@Mock
	private ExpertRepository expertRepository;

	@Mock
	private MemberRequestEstimateRepository memberRequestEstimateRepository;

	private Expert expert;
	private Member member;
	private MainItem mainItem;
	private SubItem subItem;
	private MemberRequestEstimate memberRequestEstimate;
	private ExpertResponseEstimate expertResponseEstimate;

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

		memberRequestEstimate = MemberRequestEstimate.builder()
			.member(member)
			.subItem(subItem)
			.preferredStartDate(LocalDateTime.now().plusDays(1))
			.detailedDescription("메시가 되고 싶어요")
			.build();

		expertResponseEstimate = ExpertResponseEstimate.builder()
			.totalCost(10000)
			.expert(expert)
			.memberRequestEstimate(memberRequestEstimate)
			.description("메시를 만들어 드립니다")
			.build();
	}

	@Test
	@DisplayName("고수 응답 견적서 생성 성공")
	void createExpertEstimateSuccessTest() throws Exception {

		//given
		Long expertId = 1L;
		ExpertResponseEstimateCreateRequest request =
			new ExpertResponseEstimateCreateRequest(memberRequestEstimate.getId(), 100, "메시를 만들어 드립니다.", true);

		when(expertResponseEstimateRepository.save(any(ExpertResponseEstimate.class)))
			.thenReturn(expertResponseEstimate);
		when(expertRepository.findById(expertId))
			.thenReturn(Optional.of(expert));
		when(memberRequestEstimateRepository.findById(memberRequestEstimate.getId()))
			.thenReturn(Optional.of(memberRequestEstimate));

		//when
		ExpertResponseEstimateResponse expertResponseEstimateResponse = expertResponseEstimateService.create(expertId,
			request);

		//then
		assertThat(expertResponseEstimateResponse.totalCost()).isEqualTo(request.totalCost());
		assertThat(expertResponseEstimateResponse.memberRequestEstimateId()).isEqualTo(memberRequestEstimate.getId());
	}

	@Test
	@DisplayName("고수 견적서 생성 실패 - 존재하지 않는 고수")
	void createExpertEstimateFailTest_notFoundExpert() throws Exception {

		//given
		ExpertResponseEstimateCreateRequest request =
			new ExpertResponseEstimateCreateRequest(memberRequestEstimate.getId(), 100, "메시를 만들어 드립니다.", true);

		when(expertRepository.findById(anyLong()))
			.thenReturn(Optional.empty());

		//when -> then
		assertThrows(EntityNotFoundException.class,
			() -> expertResponseEstimateService.create(1L, request));
	}

	@Test
	@DisplayName("고수 견적서 생성 실패 - 존재하지 않는 고객의 요청서")
	void createExpertEstimateFailTest_notFoundMemberEstimate() throws Exception {

		//given
		ExpertResponseEstimateCreateRequest request =
			new ExpertResponseEstimateCreateRequest(999L, 100, "메시를 만들어 드립니다.", true);

		when(expertRepository.findById(anyLong()))
			.thenReturn(Optional.of(expert));
		when(memberRequestEstimateRepository.findById(request.memberRequestEstimateId()))
			.thenReturn(Optional.empty());

		//when -> then
		assertThrows(EntityNotFoundException.class,
			() -> expertResponseEstimateService.create(1L, request));
	}

	@Test
	@DisplayName("고수 견적서 전체 조회 성공")
	void findAllSuccessTest() throws Exception {

		//given
		List<ExpertResponseEstimate> estimates = Arrays.asList(expertResponseEstimate);

		when(expertResponseEstimateRepository.findAll())
			.thenReturn(estimates);

		//when
		ExpertResponseEstimatesResponse estimatesResponse = expertResponseEstimateService.findAll();

		//then
		assertThat(estimatesResponse.expertResponseEstimatesResponse()).hasSize(1);
		assertThat(estimatesResponse.expertResponseEstimatesResponse().get(0).expertId()).isEqualTo(expert.getId());
		assertThat(estimatesResponse.expertResponseEstimatesResponse().get(0).totalCost()).isEqualTo(
			expertResponseEstimate.getTotalCost());
	}

	@Test
	@DisplayName("ID로 고수 견적서 조회 성공")
	void findExpertEstimateByIdSuccessTest() throws Exception {

		//given
		Long expertEstimateId = expertResponseEstimate.getId();
		when(expertResponseEstimateRepository.findById(expertEstimateId))
			.thenReturn(Optional.of(expertResponseEstimate));

		//when
		ExpertResponseEstimateResponse estimateResponse = expertResponseEstimateService.findById(expertEstimateId);

		//then
		assertThat(estimateResponse.id()).isEqualTo(expertResponseEstimate.getId());
		assertThat(estimateResponse.totalCost()).isEqualTo(expertResponseEstimate.getTotalCost());
		assertThat(estimateResponse.expertId()).isEqualTo(expertResponseEstimate.getExpert().getId());
		assertThat(estimateResponse.memberRequestEstimateId()).isEqualTo(
			expertResponseEstimate.getMemberRequestEstimate().getId());
	}

	@Test
	@DisplayName("ID로 고수 견적서 조회 실패 - 존재하지 않는 견적서")
	void findExpertEstimateByIdFialTest_notFoundExpertEstimate() throws Exception {

		//given
		Long expertEstimateId = expertResponseEstimate.getId();
		when(expertResponseEstimateRepository.findById(expertEstimateId))
			.thenReturn(Optional.empty());

		//when -> then
		assertThrows(EntityNotFoundException.class,
			() -> expertResponseEstimateService.findById(expertEstimateId));
	}

	@Test
	@DisplayName("고수 견적서 수정 성공")
	void updateExpertEstimateSuccessTest() throws Exception {

		//given
		int newTotalCost = 333;
		String newDescription = "호날두를 만들어 드립니다.";
		ExpertResponseEstimateUpdateRequest updateRequest = new ExpertResponseEstimateUpdateRequest(
			newTotalCost, newDescription, true);

		when(expertResponseEstimateRepository.findById(null))
			.thenReturn(Optional.of(expertResponseEstimate));

		//when
		expertResponseEstimateService.update(expertResponseEstimate.getId(), updateRequest);

		//then
		assertThat(expertResponseEstimate.getTotalCost()).isEqualTo(newTotalCost);
		assertThat(expertResponseEstimate.getDescription()).isEqualTo(newDescription);
	}

	@Test
	@DisplayName("고수 견적서 수정 실패 - 존재하지 않는 고수 견적서")
	void updateExpertEstimateFailTest_notFoundExpertEstimate() throws Exception {

		//given
		int newTotalCost = 333;
		String newDescription = "호날두를 만들어 드립니다.";
		ExpertResponseEstimateUpdateRequest updateRequest = new ExpertResponseEstimateUpdateRequest(
			newTotalCost, newDescription, true);

		when(expertResponseEstimateRepository.findById(null))
			.thenReturn(Optional.empty());

		//when -> then
		assertThrows(EntityNotFoundException.class,
			() -> expertResponseEstimateService.update(expertResponseEstimate.getId(), updateRequest));
	}

	@Test
	@DisplayName("고수 견적서 삭제 성공")
	void deleteExpertEstimateSuccessTest() throws Exception {

		//given
		when(expertResponseEstimateRepository.findById(null))
			.thenReturn(Optional.of(expertResponseEstimate));

		//when
		assertDoesNotThrow(() -> expertResponseEstimateService.delete(expertResponseEstimate.getId()));

		//then
		verify(expertResponseEstimateRepository, times(1)).delete(expertResponseEstimate);
	}
}
