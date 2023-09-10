package com.foo.gosucatcher.domain.expert.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.doNothing;
import static org.mockito.BDDMockito.doThrow;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foo.gosucatcher.domain.expert.application.ExpertService;
import com.foo.gosucatcher.domain.expert.application.dto.request.ExpertCreateRequest;
import com.foo.gosucatcher.domain.expert.application.dto.request.ExpertSubItemRequest;
import com.foo.gosucatcher.domain.expert.application.dto.request.ExpertUpdateRequest;
import com.foo.gosucatcher.domain.expert.application.dto.response.ExpertResponse;
import com.foo.gosucatcher.domain.expert.application.dto.response.ExpertsResponse;
import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.domain.member.domain.MemberRepository;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.BusinessException;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;

@WebMvcTest(ExpertController.class)
class ExpertControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	ExpertService expertService;

	@MockBean
	MemberRepository memberRepository;

	@Mock
	private Member member;

	private ExpertCreateRequest expertCreateRequest;

	@BeforeEach
	void setUp() {
		given(memberRepository.findById(1L)).willReturn(Optional.of(member));

		expertCreateRequest = new ExpertCreateRequest("업체명1", "위치1", 100, "부가설명1");

	}

	@Test
	@DisplayName("고수 등록 성공")
	void createExpertSuccessTest() throws Exception {
		ExpertResponse expertResponse = new ExpertResponse(1L, "업체명1", "위치1", 100, "부가설명1");
		given(expertService.create(any(ExpertCreateRequest.class), eq(1L))).willReturn(expertResponse);

		mockMvc.perform(
				post("/api/v1/experts").contentType(MediaType.APPLICATION_JSON).param("memberId", "1")
					.content(objectMapper.writeValueAsString(expertCreateRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(1L))
			.andExpect(jsonPath("$.storeName").value("업체명1"))
			.andExpect(jsonPath("$.location").value("위치1"))
			.andExpect(jsonPath("$.maxTravelDistance").value(100))
			.andExpect(jsonPath("$.description").value("부가설명1"))
			.andDo(print());
	}

	@Test
	@DisplayName("고수 등록 실패: 존재하지 않는 회원 ID")
	void createExpertFailTest_notFoundMember() throws Exception {
		// given
		given(expertService.create(any(ExpertCreateRequest.class), eq(9999L)))
			.willThrow(new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));
		ExpertCreateRequest request = new ExpertCreateRequest("업체명1", "위치1", 100, "부가설명1");

		// when -> then
		mockMvc.perform(post("/api/v1/experts")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.param("memberId", "9999"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("M001"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("존재하지 않는 회원입니다."))
			.andDo(print());
	}

	@Test
	@DisplayName("고수 등록 실패: 중복된 상점명")
	void createExpertFailTest_duplication() throws Exception {

		ExpertCreateRequest duplicatedExpertCreateRequest = new ExpertCreateRequest("업체명1", "위치1", 100, "부가설명1");

		given(expertService.create(any(ExpertCreateRequest.class), eq(1L)))
			.willThrow(new EntityNotFoundException(ErrorCode.DUPLICATED_EXPERT_STORENAME));

		mockMvc.perform(post("/api/v1/experts")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(duplicatedExpertCreateRequest))
				.param("memberId", "1"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("E002"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("상점명이 중복될 수 없습니다."))
			.andDo(print());
	}

	@Test
	@DisplayName("고수 ID로 조회 성공")
	void getExpertByIdSuccessTest() throws Exception {
		ExpertResponse expertResponse = new ExpertResponse(1L, "업체명1", "위치1", 100, "부가설명1");
		given(expertService.findById(1L)).willReturn(expertResponse);

		mockMvc.perform(get("/api/v1/experts/1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(1L))
			.andExpect(jsonPath("$.storeName").value("업체명1"))
			.andExpect(jsonPath("$.location").value("위치1"))
			.andExpect(jsonPath("$.maxTravelDistance").value(100))
			.andExpect(jsonPath("$.description").value("부가설명1"))
			.andDo(print());
	}

	@Test
	@DisplayName("고수 ID로 조회 실패: 존재하지 않는 고수 ID")
	void getExpertByIdFailTest_notFoundExpert() throws Exception {
		given(expertService.findById(eq(9999L))).willThrow(new EntityNotFoundException(ErrorCode.NOT_FOUND_EXPERT));

		mockMvc.perform(get("/api/v1/experts/9999"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("E001"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("존재하지 않는 고수입니다."))
			.andDo(print());
	}

	@Test
	@DisplayName("고수 전체 조회 성공")
	void getAllExpertsSuccessTest() throws Exception {
		List<Expert> expertList = List.of(new Expert(member, "업체명1", "위치1", 100, "부가설명1"));
		ExpertsResponse expertsResponse = ExpertsResponse.from(expertList);
		given(expertService.findAll()).willReturn(expertsResponse);

		mockMvc.perform(get("/api/v1/experts"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.expertsResponse[0].storeName").value("업체명1"))
			.andExpect(jsonPath("$.expertsResponse[0].location").value("위치1"))
			.andExpect(jsonPath("$.expertsResponse[0].maxTravelDistance").value(100))
			.andExpect(jsonPath("$.expertsResponse[0].description").value("부가설명1"))
			.andDo(print());
	}

	@Test
	@DisplayName("고수 수정 성공")
	void updateExpertSuccessTest() throws Exception {
		ExpertUpdateRequest updateRequest = new ExpertUpdateRequest("새로운 업체명", "새로운 위치", 150, "새로운 부가설명");
		ExpertResponse expertResponse = new ExpertResponse(1L, "새로운 업체명", "새로운 위치", 150, "새로운 부가설명");

		given(expertService.update(1L, updateRequest)).willReturn(1L);

		mockMvc.perform(patch("/api/v1/experts/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").value("1"))
			.andDo(print());
	}

	@Test
	@DisplayName("고수 수정 실패: 존재하지 않는 고수 ID")
	void updateExpertFailTest_notFoundExpert() throws Exception {
		ExpertUpdateRequest updateRequest = new ExpertUpdateRequest("새로운 업체명", "새로운 위치", 150, "새로운 부가설명");
		given(expertService.update(eq(9999L), any(ExpertUpdateRequest.class)))
			.willThrow(new EntityNotFoundException(ErrorCode.NOT_FOUND_EXPERT));

		mockMvc.perform(patch("/api/v1/experts/9999")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequest)))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("E001"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("존재하지 않는 고수입니다."))
			.andDo(print());
	}

	@Test
	@DisplayName("고수 삭제 성공")
	void deleteExpertSuccessTest() throws Exception {
		doNothing().when(expertService).delete(1L);

		mockMvc.perform(delete("/api/v1/experts/1")).andExpect(status().isOk()).andDo(print());
	}

	@Test
	@DisplayName("고수 삭제 실패: 존재하지 않는 고수 ID")
	void deleteExpertFailTest_notFoundExpert() throws Exception {
		doThrow(new EntityNotFoundException(ErrorCode.NOT_FOUND_EXPERT)).when(expertService).delete(eq(9999L));

		mockMvc.perform(delete("/api/v1/experts/9999"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("E001"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("존재하지 않는 고수입니다."))
			.andDo(print());
	}

	@Test
	@DisplayName("고수 서브 아이템 추가 성공")
	void addSubItemSuccessTest() throws Exception {
		//given
		ExpertSubItemRequest request = new ExpertSubItemRequest("세부 서비스 이름");
		Long expertId = 1L;

		given(expertService.addSubItem(eq(expertId), any(ExpertSubItemRequest.class)))
			.willReturn(expertId);

		//when -> then
		mockMvc.perform(post("/api/v1/experts/{id}/sub-items", expertId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").value(expertId))
			.andDo(print());
	}

	@Test
	@DisplayName("고수 서브 아이템 추가 실패 - 존재하지 않는 고수 or 서비스")
	void addSubItemFailTest() throws Exception {
		//given
		ExpertSubItemRequest request = new ExpertSubItemRequest("잘못된 세부 서비스 이름");
		Long expertId = 1L;

		given(expertService.addSubItem(eq(expertId), any(ExpertSubItemRequest.class)))
			.willThrow(new BusinessException(ErrorCode.ALREADY_REGISTERED_BY_SUB_ITEM));

		//when -> then
		mockMvc.perform(post("/api/v1/experts/{id}/sub-items", expertId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("E004"))
			.andExpect(jsonPath("$.message").value("해당 서비스로는 이미 등록되어있습니다."))
			.andDo(print());
	}

	@Test
	@DisplayName("고수 서브 아이템 삭제 성공")
	void removeSubItemSuccessTest() throws Exception {
		//given
		ExpertSubItemRequest request = new ExpertSubItemRequest("세부 서비스 이름");
		Long expertId = 1L;

		doNothing().when(expertService).removeSubItem(eq(expertId), any(ExpertSubItemRequest.class));

		//when
		mockMvc.perform(delete("/api/v1/experts/{id}/sub-items", expertId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isNoContent())
			.andDo(print());
	}

	@Test
	@DisplayName("고수 서브 아이템 삭제 실패 - 등록하지 않은 서비스")
	void removeSubItemFailTest() throws Exception {
		//given
		ExpertSubItemRequest request = new ExpertSubItemRequest("등록하지 않은 세부 서비스 이름");
		Long expertId = 1L;

		doThrow(new BusinessException(ErrorCode.NOT_FOUND_EXPERT_ITEM))
			.when(expertService).removeSubItem(anyLong(), any(ExpertSubItemRequest.class));

		//when
		mockMvc.perform(delete("/api/v1/experts/{id}/sub-items", expertId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("EI001"))
			.andExpect(jsonPath("$.message").value("해당 고수는 요청한 서비스를 등록하지 않았습니다."))
			.andDo(print());
	}
}
