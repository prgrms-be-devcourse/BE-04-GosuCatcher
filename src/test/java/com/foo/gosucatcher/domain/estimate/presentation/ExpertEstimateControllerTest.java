package com.foo.gosucatcher.domain.estimate.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foo.gosucatcher.domain.estimate.application.ExpertEstimateService;
import com.foo.gosucatcher.domain.estimate.application.dto.request.ExpertNormalEstimateCreateRequest;
import com.foo.gosucatcher.domain.estimate.application.dto.request.MemberRequestEstimateRequest;
import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertEstimateResponse;
import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertEstimatesResponse;
import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertNormalEstimateResponse;
import com.foo.gosucatcher.domain.estimate.application.dto.response.MemberRequestEstimateResponse;
import com.foo.gosucatcher.domain.expert.application.dto.response.ExpertResponse;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExpertEstimateController.class)
class ExpertEstimateControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	ExpertEstimateService expertEstimateService;

	private ExpertNormalEstimateCreateRequest expertNormalEstimateCreateRequest;
	private ExpertResponse expertResponse;
	private MemberRequestEstimateRequest memberRequestEstimateRequest;
	private MemberRequestEstimateResponse memberRequestEstimateResponse;
	private String baseUrl = "/api/v1/expert-estimates";

	@BeforeEach
	void setUp() {
		expertNormalEstimateCreateRequest =
			new ExpertNormalEstimateCreateRequest(100, "서울시 강남구", "상세설명을씁니다");
		expertResponse =
			new ExpertResponse(1L, "상점이름입니다", "서울시 강남구", 10, "설명입니다여긴");

		memberRequestEstimateRequest = new MemberRequestEstimateRequest(1L,
			"서울 강남구 개포1동", LocalDateTime.now().plusDays(3), "추가 내용");

		memberRequestEstimateResponse = new MemberRequestEstimateResponse(1L, 1L,
			1L, "서울 강남구 개포1동", LocalDateTime.now().plusDays(4), "추가 내용");
	}

	@Test
	@DisplayName("고수 일반 견적서 등록 성공")
	void createExpertEstimateSuccessTest() throws Exception {

		//given
		ExpertNormalEstimateResponse expertNormalEstimateResponse = new ExpertNormalEstimateResponse(1L, expertResponse, memberRequestEstimateResponse,
			100, "서울시 강남구", "상세설명을씁니다");
		given(expertEstimateService.createNormal(anyLong(), anyLong(), any()))
			.willReturn(expertNormalEstimateResponse);

		//when -> then
		mockMvc.perform(post(baseUrl + "/normal/{expertId}?memberEstimateId={memberEstimateId}", 1L, 1L)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(expertNormalEstimateCreateRequest)))
			.andExpect(jsonPath("$.id").value(1))
			.andExpect(jsonPath("$.expertResponse.id").value(1))
			.andExpect(jsonPath("$.totalCost").value(100))
			.andExpect(jsonPath("$.description").value("상세설명을씁니다"))
			.andExpect(jsonPath("$.activityLocation").value("서울시 강남구"))
			.andExpect(jsonPath("$.memberRequestEstimateResponse.id").value(1))
			.andExpect(jsonPath("$.memberRequestEstimateResponse.memberId").value(1))
			.andExpect(jsonPath("$.memberRequestEstimateResponse.subItemId").value(1))
			.andExpect(jsonPath("$.memberRequestEstimateResponse.location").value("서울 강남구 개포1동"))
			.andExpect(jsonPath("$.memberRequestEstimateResponse.detailedDescription").value("추가 내용"))
			.andDo(print());
	}

	@Test
	@DisplayName("고수 견적서 등록 실패 - 존재하지 않는 고수")
	void createExpertEstimateFailTest_notFoundExpert() throws Exception {

		//given
		given(expertEstimateService.createNormal(anyLong(), anyLong(), any()))
			.willThrow(new EntityNotFoundException(ErrorCode.NOT_FOUND_EXPERT));

		//when -> then
		mockMvc.perform(post(baseUrl + "/normal/{expertId}?memberEstimateId={memberEstimateId}", 1L, 1L)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(expertNormalEstimateCreateRequest)))
			.andExpect(status().isNotFound())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("E001"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("존재하지 않는 고수입니다."))
			.andDo(print());
	}

	@Test
	@DisplayName("고수 견적서 등록 실패 - 존재하지 않는 고객 요청 견적서")
	void createExpertEstimateFailTest_notFoundMemberRequestEstimate() throws Exception {

		//given
		given(expertEstimateService.createNormal(anyLong(), anyLong(), any()))
			.willThrow(new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER_REQUEST_ESTIMATE));

		//when -> then
		mockMvc.perform(post(baseUrl + "/normal/{expertId}?memberEstimateId={memberEstimateId}", 1L, 1L)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(expertNormalEstimateCreateRequest)))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("존재하지 않는 회원 요청 견적서입니다."))
			.andDo(print());
	}

	@Test
	@DisplayName("고수 견적서 등록 실패 - 잘못된 값 입력")
	void createExpertEstimateFailTest_invalidValue() throws Exception {

		//given
		expertNormalEstimateCreateRequest =
			new ExpertNormalEstimateCreateRequest(100, "서울시 강남구", "짧은 설명");

		//when -> then
		mockMvc.perform(post(baseUrl + "/normal/{expertId}?memberEstimateId={memberEstimateId}", 1L, 1L)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(expertNormalEstimateCreateRequest)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("C001"))
			.andExpect(jsonPath("$.errors[0].value").value("짧은 설명"))
			.andExpect(jsonPath("$.errors[0].reason").value("견적서에 대한 설명은 6자 이상 적어주세요."))
			.andExpect(jsonPath("$.message").value("잘못된 값을 입력하셨습니다."))
			.andDo(print());
	}

	@Test
	@DisplayName("고수 응답 견적서 전체 조회")
	void findAllSuccessTest() throws Exception {

		//given
		ExpertEstimatesResponse estimatesResponse = new ExpertEstimatesResponse(
			List.of(new ExpertEstimateResponse(1L, expertResponse, memberRequestEstimateResponse, 100, "서울시 강남구", "설명을 적어보세요")
			));
		given(expertEstimateService.findAll()).willReturn(estimatesResponse);

		//when -> then
		mockMvc.perform(get(baseUrl))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.expertEstimateResponseList[0].id").value(1))
			.andExpect(jsonPath("$.expertEstimateResponseList[0].totalCost").value(100))
			.andDo(print());
	}

	@Test
	@DisplayName("고수 응답 견적서 ID로 조회 성공")
	void findExpertEstimateByIdSuccessTest() throws Exception {

		//given
		ExpertEstimateResponse expertNormalEstimateResponse = new ExpertEstimateResponse(1L, expertResponse, memberRequestEstimateResponse, 100, "서울시 강남구", "설명을 적어보세요");
		given(expertEstimateService.findById(anyLong())).willReturn(expertNormalEstimateResponse);

		//when -> then
		mockMvc.perform(get(baseUrl + "/{id}", 1L))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(1))
			.andExpect(jsonPath("$.expertResponse.id").value(1))
			.andExpect(jsonPath("$.totalCost").value(100))
			.andExpect(jsonPath("$.description").value("설명을 적어보세요"))
			.andExpect(jsonPath("$.activityLocation").value("서울시 강남구"))
			.andExpect(jsonPath("$.memberRequestEstimateResponse.id").value(1))
			.andExpect(jsonPath("$.memberRequestEstimateResponse.memberId").value(1))
			.andExpect(jsonPath("$.memberRequestEstimateResponse.subItemId").value(1))
			.andExpect(jsonPath("$.memberRequestEstimateResponse.location").value("서울 강남구 개포1동"))
			.andExpect(jsonPath("$.memberRequestEstimateResponse.detailedDescription").value("추가 내용"))
			.andDo(print());
	}

	@Test
	@DisplayName("고수 응답 견적서 ID로 조회 실패 - 존재하지 않는 고수 응답 견적서")
	void findExpertEstimateByIdFailTest_notFoundExpertEstimate() throws Exception {

		//given
		given(expertEstimateService.findById(anyLong()))
			.willThrow(new EntityNotFoundException(ErrorCode.NOT_FOUND_EXPERT_ESTIMATE));

		//when -> then
		mockMvc.perform(get(baseUrl + "/{id}", 1L))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("EE001"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("존재하지 않는 고수가 응답한 견적서 입니다."))
			.andDo(print());
	}
}
