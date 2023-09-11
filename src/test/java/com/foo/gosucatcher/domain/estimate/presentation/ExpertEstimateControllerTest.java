package com.foo.gosucatcher.domain.estimate.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foo.gosucatcher.domain.estimate.application.ExpertEstimateService;
import com.foo.gosucatcher.domain.estimate.application.dto.request.ExpertEstimateCreateRequest;
import com.foo.gosucatcher.domain.estimate.application.dto.request.ExpertEstimateUpdateRequest;
import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertEstimateResponse;
import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertEstimatesResponse;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;

@WebMvcTest(ExpertEstimateController.class)
class ExpertEstimateControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	ExpertEstimateService expertEstimateService;

	private ExpertEstimateCreateRequest expertEstimateCreateRequest;
	private String baseUrl = "/api/v1/expert-response-estimates";

	@BeforeEach
	void setUp() {
		expertEstimateCreateRequest = new ExpertEstimateCreateRequest(1L, 1L, 100, "상세설명을씁니다", true);
	}

	@Test
	@DisplayName("고수 응답 견적서 등록 성공")
	void createExpertEstimateSuccessTest() throws Exception {

		//given
		ExpertEstimateResponse expertEstimateResponse = new ExpertEstimateResponse(1L, 1L, 1L, 100, "상세설명을씁니다", true);
		given(expertEstimateService.create(anyLong(), any(ExpertEstimateCreateRequest.class))).willReturn(
			expertEstimateResponse);

		//when -> then
		mockMvc.perform(post(baseUrl + "/{id}", 1L).contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(expertEstimateCreateRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(1))
			.andExpect(jsonPath("$.memberEstimateId").value(1))
			.andExpect(jsonPath("$.totalCost").value(100))
			.andExpect(jsonPath("$.description").value("상세설명을씁니다"))
			.andExpect(jsonPath("$.isOftenUsed").value(true))
			.andDo(print());
	}

	@Test
	@DisplayName("고수 응답 견적서 등록 실패 - 존재하지 않는 고수")
	void createExpertEstimateFailTest_notFoundExpert() throws Exception {

		//given
		given(expertEstimateService.create(anyLong(), any(ExpertEstimateCreateRequest.class))).willThrow(
			new EntityNotFoundException(ErrorCode.NOT_FOUND_EXPERT));

		//when -> then
		mockMvc.perform(post(baseUrl + "/{id}", 1L).contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(expertEstimateCreateRequest)))
			.andExpect(status().isNotFound())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("E001"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("존재하지 않는 고수입니다."))
			.andDo(print());
	}

	@Test
	@DisplayName("고수 응답 견적서 등록 실패 - 존재하지 않는 고객 요청 견적서")
	void createExpertEstimateFailTest_notFoundMemberEstimate() throws Exception {

		//given
		given(expertEstimateService.create(anyLong(), any(ExpertEstimateCreateRequest.class))).willThrow(
			new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER_ESTIMATE));

		//when -> then
		mockMvc.perform(post(baseUrl + "/{id}", 1L).contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(expertEstimateCreateRequest)))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("MRE001"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("존재하지 않는 회원 요청 견적서입니다."))
			.andDo(print());
	}

	@Test
	@DisplayName("고수 응답 견적서 등록 실패 - 잘못된 값 입력")
	void createExpertEstimateFailTest_invalidValue() throws Exception {

		//given
		expertEstimateCreateRequest = new ExpertEstimateCreateRequest(1L, 1L, 100, "짧은 설명", true);

		//when -> then
		mockMvc.perform(post(baseUrl + "/{id}", 1L).contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(expertEstimateCreateRequest)))
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
			List.of(new ExpertEstimateResponse(1L, 1L, 1L, 100, "설명을 적어보세요", true)));
		given(expertEstimateService.findAll()).willReturn(estimatesResponse);

		//when -> then
		mockMvc.perform(get(baseUrl))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.expertResponseEstimatesResponse[0].id").value(1))
			.andExpect(jsonPath("$.expertResponseEstimatesResponse[0].totalCost").value(100))
			.andDo(print());
	}

	@Test
	@DisplayName("고수 응답 견적서 ID로 조회 성공")
	void findExpertEstimateByIdSuccessTest() throws Exception {

		//given
		ExpertEstimateResponse expertEstimateResponse = new ExpertEstimateResponse(1L, 1L, 1L, 100, "설명을 적어보세요", true);
		given(expertEstimateService.findById(anyLong())).willReturn(expertEstimateResponse);

		//when -> then
		mockMvc.perform(get(baseUrl + "/{id}", 1L))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.expertId").value(1))
			.andExpect(jsonPath("$.totalCost").value(100))
			.andExpect(jsonPath("$.isOftenUsed").value(true))
			.andDo(print());
	}

	@Test
	@DisplayName("고수 응답 견적서 ID로 조회 실패 - 존재하지 않는 고수 응답 견적서")
	void findExpertEstimateByIdFailTest_notFoundExpertEstimate() throws Exception {

		//given
		given(expertEstimateService.findById(anyLong())).willThrow(
			new EntityNotFoundException(ErrorCode.NOT_FOUND_EXPERT_RESPONSE_ESTIMATE));

		//when -> then
		mockMvc.perform(get(baseUrl + "/{id}", 1L))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("ERE001"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("존재하지 않는 고수가 응답한 견적서 입니다."))
			.andDo(print());
	}

	@Test
	@DisplayName("고수 응답 견적서 수정 성공")
	void updateExpertEstimateSuccessTest() throws Exception {

		//given
		ExpertEstimateUpdateRequest updateRequest = new ExpertEstimateUpdateRequest(9999, "수정한 설명입니다.", false);
		ExpertEstimateResponse expertEstimateResponse = new ExpertEstimateResponse(1L, 1L, 1L, 999, "수정한 설명입니다.",
			false);

		given(expertEstimateService.update(anyLong(), any())).willReturn(expertEstimateResponse.id());

		//when -> then
		mockMvc.perform(patch(baseUrl + "/{id}", 1L, updateRequest).contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequest)))
			.andExpect(status().isOk())
			.andExpect(content().string("1"))
			.andDo(print());
	}
}
