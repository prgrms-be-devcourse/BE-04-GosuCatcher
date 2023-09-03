package com.foo.gosucatcher.domain.expert.presentation;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import com.foo.gosucatcher.domain.expert.application.dto.request.ExpertUpdateRequest;
import com.foo.gosucatcher.domain.expert.application.dto.response.ExpertResponse;
import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.domain.member.domain.MemberRepository;

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
	private Member member1;

	@Mock
	private Member member2;

	private ExpertCreateRequest expertCreateRequest;

	@BeforeEach
	void setUp() {
		given(memberRepository.findById(1L)).willReturn(Optional.of(member1));

		expertCreateRequest = new ExpertCreateRequest("가게이름1", "위치1", 100, "부가설명1");

	}

	@Test
	@DisplayName("고수 등록 성공")
	void createExpertSuccessTest() throws Exception {
		ExpertResponse expertResponse = new ExpertResponse(1L, "가게이름1", "위치1", 100, "부가설명1");
		given(expertService.create(any(ExpertCreateRequest.class), eq(1L))).willReturn(expertResponse);

		mockMvc.perform(
				post("/api/v1/experts").contentType(MediaType.APPLICATION_JSON).param("memberId", "1")  // memberId 파라미터 추가
					.content(objectMapper.writeValueAsString(expertCreateRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(1L))
			.andExpect(jsonPath("$.storeName").value("가게이름1"))
			.andExpect(jsonPath("$.location").value("위치1"))
			.andExpect(jsonPath("$.distance").value(100))
			.andExpect(jsonPath("$.description").value("부가설명1"))
			.andDo(print());
	}

	@Test
	@DisplayName("고수 ID로 조회 성공")
	void getExpertByIdSuccessTest() throws Exception {
		ExpertResponse expertResponse = new ExpertResponse(1L, "가게이름1", "위치1", 100, "부가설명1");
		given(expertService.findById(1L)).willReturn(expertResponse);

		mockMvc.perform(get("/api/v1/experts/1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(1L))
			.andExpect(jsonPath("$.storeName").value("가게이름1"))
			.andDo(print());
	}

	@Test
	@DisplayName("고수 전체 조회 성공")
	void getAllExpertsSuccessTest() throws Exception {
		List<Expert> expertList = List.of(new Expert(member1, "가게이름1", "위치1", 100, "부가설명1"),
			new Expert(member2, "가게이름2", "위치2", 200, "부가설명2"));
		given(expertService.findAll()).willReturn(expertList);

		mockMvc.perform(get("/api/v1/experts"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.expertsResponse[0].storeName").value("가게이름1"))
			.andExpect(jsonPath("$.expertsResponse[1].storeName").value("가게이름2"))
			.andDo(print());
	}

	@Test
	@DisplayName("고수 수정 성공")
	void updateExpertSuccessTest() throws Exception {
		ExpertUpdateRequest updateRequest = new ExpertUpdateRequest("새로운 가게이름", "새로운 위치", 150, "새로운 부가설명");
		ExpertResponse expertResponse = new ExpertResponse(1L, "새로운 가게이름", "새로운 위치", 150, "새로운 부가설명");

		given(expertService.update(1L, updateRequest)).willReturn(1L);

		mockMvc.perform(patch("/api/v1/experts/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").value("1"))
			.andDo(print());
	}

	@Test
	@DisplayName("고수 삭제 성공")
	void deleteExpertSuccessTest() throws Exception {
		doNothing().when(expertService).delete(1L);

		mockMvc.perform(delete("/api/v1/experts/1")).andExpect(status().isNoContent()).andDo(print());
	}
}
