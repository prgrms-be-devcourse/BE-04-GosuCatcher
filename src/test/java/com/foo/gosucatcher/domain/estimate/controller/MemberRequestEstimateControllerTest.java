package com.foo.gosucatcher.domain.estimate.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foo.gosucatcher.domain.estimate.domain.MemberRequestEstimate;
import com.foo.gosucatcher.domain.estimate.dto.request.MemberRequestEstimateRequest;
import com.foo.gosucatcher.domain.estimate.dto.response.MemberRequestEstimateResponse;
import com.foo.gosucatcher.domain.estimate.dto.response.MemberRequestEstimatesResponse;
import com.foo.gosucatcher.domain.estimate.service.MemberRequestEstimateService;
import com.foo.gosucatcher.domain.item.domain.MainItem;
import com.foo.gosucatcher.domain.item.domain.SubItem;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;

@WebMvcTest(MemberRequestEstimateController.class)
class MemberRequestEstimateControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private MemberRequestEstimateService memberRequestEstimateService;

	@DisplayName("회원 요청 견적서 등록 성공 테스트")
	@Test
	void create() throws Exception {
		//given
		Long memberId = 1L;
		Long subItemId = 1L;

		MemberRequestEstimateRequest memberRequestEstimateRequest = new MemberRequestEstimateRequest("서울 강남구 개포1동",
			LocalDateTime.now(), "추가 내용");

		MemberRequestEstimateResponse memberRequestEstimateResponse = new MemberRequestEstimateResponse(1L, memberId,
			subItemId, "서울 강남구 개포1동", LocalDateTime.now(), "추가 내용");

		when(memberRequestEstimateService.create(memberId, subItemId, memberRequestEstimateRequest)).thenReturn(
			memberRequestEstimateResponse);

		//when
		//then
		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/member-request-estimates")
				.param("member", memberId.toString())
				.param("subItem", subItemId.toString())
				.content(objectMapper.writeValueAsString(memberRequestEstimateRequest))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(1L))
			.andExpect(jsonPath("$.memberId").value(memberRequestEstimateResponse.memberId()))
			.andExpect(jsonPath("$.subItemId").value(memberRequestEstimateResponse.subItemId()))
			.andExpect(jsonPath("$.location").value(memberRequestEstimateResponse.location()))
			.andExpect(jsonPath("$.detailedDescription").value(memberRequestEstimateResponse.detailedDescription()));
	}

	@DisplayName("회원 요청 견적서 등록 실패 테스트")
	@Test
	void createFailed() throws Exception {
		//given
		Long memberId = 1L;
		Long subItemId = 1L;
		MemberRequestEstimateRequest memberRequestEstimateRequest = new MemberRequestEstimateRequest(" ",
			LocalDateTime.now(), "추가 내용");

		MemberRequestEstimateResponse memberRequestEstimateResponse = new MemberRequestEstimateResponse(1L, memberId,
			subItemId, " ", LocalDateTime.now(), "추가 내용");

		when(memberRequestEstimateService.create(memberId, subItemId, memberRequestEstimateRequest)).thenReturn(
			memberRequestEstimateResponse);

		//when
		//then
		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/member-request-estimates")
				.param("member", memberId.toString())
				.param("subItem", subItemId.toString())
				.content(objectMapper.writeValueAsString(memberRequestEstimateRequest))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("C001"))
			.andExpect(jsonPath("$.errors").isArray())
			.andExpect(jsonPath("$.errors[0].field").value("location"))
			.andExpect(jsonPath("$.errors[0].value").value(" "))
			.andExpect(jsonPath("$.errors[0].reason").value("지역을 등록해주세요."))
			.andExpect(jsonPath("$.message").value("잘못된 값을 입력하셨습니다."));
	}

	@DisplayName("회원 요청 견적서 전체 조회 성공 테스트")
	@Test
	void findAll() throws Exception {
		//given
		Long memberId = 1L;

		Member member = Member.builder()
			.name("성이름")
			.password("abcd11@@")
			.email("abcd123@abc.com")
			.phoneNumber("010-0000-0000")
			.build();

		MainItem mainItem = MainItem.builder().name("메인 서비스 이름").description("메인 서비스 설명").build();

		SubItem subItem = SubItem.builder().mainItem(mainItem).name("세부 서비스 이름").description("세부 서비스 설명").build();

		MemberRequestEstimate memberRequestEstimate = MemberRequestEstimate.builder()
			.member(member)
			.subItem(subItem)
			.location("서울 강남구 개포1동")
			.startDate(LocalDateTime.now())
			.detailedDescription("추가 내용")
			.build();

		List<MemberRequestEstimate> mockEstimates = List.of(memberRequestEstimate);
		MemberRequestEstimatesResponse mockResponse = new MemberRequestEstimatesResponse(mockEstimates);

		when(memberRequestEstimateService.findAllByMember(memberId)).thenReturn(mockResponse);

		//when
		//then
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/member-request-estimates")
				.param("member", memberId.toString())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.memberRequestEstimates").isArray())
			.andExpect(jsonPath("$.memberRequestEstimates[0].location").value("서울 강남구 개포1동"))
			.andExpect(jsonPath("$.memberRequestEstimates[0].detailedDescription").value("추가 내용"));
	}

	@DisplayName("회원 요청 견적서 단건 조회 성공 테스트")
	@Test
	void findById() throws Exception {
		//given
		Long memberRequestEstimateId = 1L;

		Long memberId = 1L;
		Long subItemId = 1L;

		MemberRequestEstimateResponse memberRequestEstimateResponse = new MemberRequestEstimateResponse(1L, memberId,
			subItemId, "서울 강남구 개포1동", LocalDateTime.now(), "추가 내용");

		when(memberRequestEstimateService.findById(memberRequestEstimateId)).thenReturn(memberRequestEstimateResponse);

		//when
		//then
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/member-request-estimates/{memberRequestEstimateId}",
				memberRequestEstimateId).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(memberRequestEstimateId))
			.andExpect(jsonPath("$.memberId").value(memberId))
			.andExpect(jsonPath("$.subItemId").value(subItemId))
			.andExpect(jsonPath("$.location").value("서울 강남구 개포1동"))
			.andExpect(jsonPath("$.detailedDescription").value("추가 내용"));
	}

	@DisplayName("회원 요청 견적서 단건 조회 실패 테스트")
	@Test
	void findByIdFailed() throws Exception {
		//given
		when(memberRequestEstimateService.findById(any(Long.class))).thenThrow(
			new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER_REQUEST_ESTIMATE));

		//when
		//then
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/member-request-estimates/{memberRequestEstimateId}", 1L)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("MRE001"))
			.andExpect(jsonPath("$.errors").isArray())
			.andExpect(jsonPath("$.message").value("존재하지 않는 회원 요청 견적서입니다."));
	}

	@DisplayName("회원 요청 견적서 수정 성공 테스트")
	@Test
	void update() throws Exception {
		//given
		Long memberRequestEstimateId = 1L;
		Long memberId = 1L;
		Long subItemId = 1L;

		MemberRequestEstimateRequest memberRequestEstimateRequest = new MemberRequestEstimateRequest("수정 지역",
			LocalDateTime.now(), "수정 내용");

		MemberRequestEstimateResponse memberRequestEstimateResponse = new MemberRequestEstimateResponse(
			memberRequestEstimateId, memberId, subItemId, "수정 지역", LocalDateTime.now(), "수정 내용");

		when(memberRequestEstimateService.update(memberRequestEstimateId, memberRequestEstimateRequest)).thenReturn(
			memberRequestEstimateResponse);

		//when
		//then
		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/member-request-estimates/{id}", memberRequestEstimateId)
				.content(objectMapper.writeValueAsString(memberRequestEstimateRequest))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id").value(memberRequestEstimateId))
			.andExpect(jsonPath("$.memberId").value(memberId))
			.andExpect(jsonPath("$.subItemId").value(subItemId))
			.andExpect(jsonPath("$.location").value("수정 지역"))
			.andExpect(jsonPath("$.detailedDescription").value("수정 내용"));
	}

	@DisplayName("회원 요청 견적서 수정 실패 테스트")
	@Test
	void updateFailed() throws Exception {
		//given
		Long memberRequestEstimateId = 1L;
		Long memberId = 1L;
		Long subItemId = 1L;

		MemberRequestEstimateRequest memberRequestEstimateRequest = new MemberRequestEstimateRequest(" ",
			LocalDateTime.now(), "수정 내용");

		MemberRequestEstimateResponse memberRequestEstimateResponse = new MemberRequestEstimateResponse(
			memberRequestEstimateId, memberId, subItemId, "수정 지역", LocalDateTime.now(), " ");

		when(memberRequestEstimateService.update(memberRequestEstimateId, memberRequestEstimateRequest)).thenReturn(
			memberRequestEstimateResponse);

		//when
		//then
		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/member-request-estimates/{id}", memberRequestEstimateId)
				.content(objectMapper.writeValueAsString(memberRequestEstimateRequest))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("C001"))
			.andExpect(jsonPath("$.errors").isArray())
			.andExpect(jsonPath("$.errors[0].field").value("location"))
			.andExpect(jsonPath("$.errors[0].value").value(" "))
			.andExpect(jsonPath("$.errors[0].reason").value("지역을 등록해주세요."))
			.andExpect(jsonPath("$.message").value("잘못된 값을 입력하셨습니다."));
	}

	@DisplayName("회원 요청 견적서 삭제 성공 테스트")
	@Test
	void delete() throws Exception {
		//given
		Long memberRequestEstimateId = 1L;

		doNothing().when(memberRequestEstimateService).delete(memberRequestEstimateId);

		// Act and Assert
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/member-request-estimates/{id}", memberRequestEstimateId)
			.contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk());
	}

	@DisplayName("회원 요청 견적서 삭제 실패 테스트")
	@Test
	void deleteFailed() throws Exception {
		//given
		doThrow(new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER_REQUEST_ESTIMATE)).when(
			memberRequestEstimateService).delete(any(Long.class));

		//when
		//then
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/member-request-estimates/{id}", 1L)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("MRE001"))
			.andExpect(jsonPath("$.errors").isArray())
			.andExpect(jsonPath("$.message").value("존재하지 않는 회원 요청 견적서입니다."));
	}
}