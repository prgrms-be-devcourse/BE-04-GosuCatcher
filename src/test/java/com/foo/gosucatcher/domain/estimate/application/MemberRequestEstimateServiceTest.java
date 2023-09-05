package com.foo.gosucatcher.domain.estimate.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.foo.gosucatcher.domain.estimate.domain.MemberRequestEstimate;
import com.foo.gosucatcher.domain.estimate.domain.MemberRequestEstimateRepository;
import com.foo.gosucatcher.domain.estimate.application.dto.request.MemberRequestEstimateRequest;
import com.foo.gosucatcher.domain.estimate.application.dto.response.MemberRequestEstimateResponse;
import com.foo.gosucatcher.domain.estimate.application.dto.response.MemberRequestEstimatesResponse;
import com.foo.gosucatcher.domain.item.domain.MainItem;
import com.foo.gosucatcher.domain.item.domain.SubItem;
import com.foo.gosucatcher.domain.item.domain.SubItemRepository;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.domain.member.domain.MemberRepository;
import com.foo.gosucatcher.global.error.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class MemberRequestEstimateServiceTest {

	@Mock
	private MemberRequestEstimateRepository memberRequestEstimateRepository;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private SubItemRepository subItemRepository;

	@InjectMocks
	private MemberRequestEstimateService memberRequestEstimateService;

	private Member member;
	private MainItem mainItem;
	private SubItem subItem;
	private MemberRequestEstimate memberRequestEstimate;

	@BeforeEach
	void setUp() {
		member = Member.builder()
			.name("성이름")
			.password("abcd11@@")
			.email("abcd123@abc.com")
			.phoneNumber("010-0000-0000")
			.build();

		mainItem = MainItem.builder().name("메인 서비스 이름").description("메인 서비스 설명").build();

		subItem = SubItem.builder().mainItem(mainItem).name("세부 서비스 이름").description("세부 서비스 설명").build();

		memberRequestEstimate = MemberRequestEstimate.builder()
			.member(member)
			.subItem(subItem)
			.location("서울 강남구 개포1동")
			.preferredStartDate(LocalDateTime.now().plusDays(1))
			.detailedDescription("추가 내용")
			.build();
	}

	@DisplayName("회원 요정 견적서 저장 성공 테스트")
	@Test
	void create() {
		//given
		Long memberId = 1L;
		Long subItemId = 1L;
		Long memberRequestEstimateId = 1L;

		MemberRequestEstimateRequest memberRequestEstimateRequest = new MemberRequestEstimateRequest(subItemId,
			memberRequestEstimate.getLocation(), memberRequestEstimate.getPreferredStartDate(),
			memberRequestEstimate.getDetailedDescription());

		when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
		when(subItemRepository.findById(subItemId)).thenReturn(Optional.of(subItem));
		when(memberRequestEstimateRepository.save(any(MemberRequestEstimate.class))).thenReturn(memberRequestEstimate);
		when(memberRequestEstimateRepository.findById(memberRequestEstimateId)).thenReturn(
			Optional.of(memberRequestEstimate));

		//when
		MemberRequestEstimateResponse memberRequestEstimateResponse = memberRequestEstimateService.create(memberId,
			memberRequestEstimateRequest);
		MemberRequestEstimate result = memberRequestEstimateRepository.findById(memberRequestEstimateId).get();

		//then
		assertThat(memberRequestEstimateResponse.location()).isEqualTo(result.getLocation());
		assertThat(memberRequestEstimateResponse.preferredStartDate()).isEqualTo(result.getPreferredStartDate());
		assertThat(memberRequestEstimateResponse.detailedDescription()).isEqualTo(result.getDetailedDescription());
	}

	@DisplayName("회원 요정 견적서 생성 실패 테스트 - 희망 시작일이 현재보다 이전인 경우")
	@Test
	void createFailed() {
		//given
		//when
		//then
		assertThrows(BusinessException.class, () -> MemberRequestEstimate.builder()
			.member(member)
			.subItem(subItem)
			.location("서울 강남구 개포1동")
			.preferredStartDate(LocalDateTime.now().minusDays(3))
			.detailedDescription("추가 내용")
			.build());
	}

	@DisplayName("회원 요정 견적서 회원별 전체 조회 테스트")
	@Test
	void findAllByMember() {
		//given
		Long memberId = 1L;

		MemberRequestEstimate memberRequestEstimate2 = MemberRequestEstimate.builder()
			.member(member)
			.subItem(subItem)
			.location("서울 강남구 개포2동")
			.preferredStartDate(LocalDateTime.now())
			.detailedDescription("추가 내용2")
			.build();

		List<MemberRequestEstimate> estimates = List.of(memberRequestEstimate, memberRequestEstimate2);

		when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
		when(memberRequestEstimateRepository.findAllByMember(member)).thenReturn(estimates);

		//when
		MemberRequestEstimatesResponse memberRequestEstimatesResponse = memberRequestEstimateService.findAllByMember(
			memberId);

		//then
		assertThat(memberRequestEstimatesResponse.memberRequestEstimates()).hasSize(2);
	}

	@DisplayName("회원 요정 견적서 회원 id로 조회 테스트")
	@Test
	void findById() {
		//given
		Long memberRequestEstimateId = 1L;

		when(memberRequestEstimateRepository.findById(memberRequestEstimateId)).thenReturn(
			Optional.of(memberRequestEstimate));

		//when
		MemberRequestEstimateResponse memberRequestEstimateResponse = memberRequestEstimateService.findById(
			memberRequestEstimateId);

		//then
		assertThat(memberRequestEstimateResponse.location()).isEqualTo(memberRequestEstimate.getLocation());
		assertThat(memberRequestEstimateResponse.preferredStartDate()).isEqualTo(
			memberRequestEstimate.getPreferredStartDate());
		assertThat(memberRequestEstimateResponse.detailedDescription()).isEqualTo(
			memberRequestEstimate.getDetailedDescription());
	}

	@DisplayName("회원 요정 견적서 수정 성공 테스트")
	@Test
	void update() {
		//given
		Long memberRequestEstimateId = 1L;
		Long subItemId = 1L;

		MemberRequestEstimateRequest memberRequestEstimateRequest = new MemberRequestEstimateRequest(subItemId, "수정 지역",
			LocalDateTime.now().plusDays(2), "수정 내용");

		when(memberRequestEstimateRepository.findById(memberRequestEstimateId)).thenReturn(
			Optional.of(memberRequestEstimate));

		//when
		memberRequestEstimateService.update(memberRequestEstimateId, memberRequestEstimateRequest);

		//then
		assertThat(memberRequestEstimateRequest.location()).isEqualTo(memberRequestEstimate.getLocation());
		assertThat(memberRequestEstimateRequest.detailedDescription()).isEqualTo(
			memberRequestEstimate.getDetailedDescription());
	}

	@DisplayName("회원 요정 견적서 수정 실패 테스트 - 희망 시작일이 현재보다 이전인 경우")
	@Test
	void updateFailed() {
		//given
		Long memberRequestEstimateId = 1L;
		Long subItemId = 1L;

		MemberRequestEstimateRequest memberRequestEstimateRequest = new MemberRequestEstimateRequest(subItemId, "수정 지역",
			LocalDateTime.now().minusDays(3), "수정 내용");

		when(memberRequestEstimateRepository.findById(memberRequestEstimateId)).thenReturn(
			Optional.of(memberRequestEstimate));

		//when
		//then
		assertThrows(BusinessException.class,
			() -> memberRequestEstimateService.update(memberRequestEstimateId, memberRequestEstimateRequest));
	}
}
