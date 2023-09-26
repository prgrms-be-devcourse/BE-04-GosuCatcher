package com.foo.gosucatcher.domain.chat.presentation;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.foo.gosucatcher.domain.chat.application.ChattingRoomService;
import com.foo.gosucatcher.domain.chat.application.dto.response.ChattingRoomResponse;
import com.foo.gosucatcher.domain.chat.application.dto.response.ChattingRoomsResponse;
import com.foo.gosucatcher.domain.estimate.application.dto.response.MemberEstimateResponse;
import com.foo.gosucatcher.domain.estimate.domain.Status;
import com.foo.gosucatcher.domain.item.application.dto.response.sub.SubItemResponse;
import com.foo.gosucatcher.domain.item.domain.MainItem;
import com.foo.gosucatcher.domain.item.domain.SubItem;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = {ChattingRoomController.class}, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class ChattingRoomControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ChattingRoomService chattingRoomService;

	private MainItem mainItem;
	private SubItem subItem;
	private SubItemResponse subItemResponse;

	@BeforeEach
	void setUp() {
		mainItem = MainItem.builder().name("메인 서비스 이름").description("메인 서비스 설명").build();

		subItem = SubItem.builder().mainItem(mainItem).name("세부 서비스 이름").description("세부 서비스 설명").build();

		subItemResponse = new SubItemResponse(1L, subItem.getMainItem().getName(), subItem.getName(), subItem.getDescription());
	}

	@DisplayName("채팅방 전체 조회 성공 테스트")
	@Test
	void findAll() throws Exception {
		//given
		MemberEstimateResponse memberEstimateResponse = new MemberEstimateResponse(1L, 1L, 1L, subItemResponse,"서울시 강남구", LocalDateTime.now().plusDays(3), "세부 설명", Status.PROCEEDING);

		ChattingRoomResponse chattingRoomResponse = new ChattingRoomResponse(1L, memberEstimateResponse);
		ChattingRoomResponse chattingRoomResponse2 = new ChattingRoomResponse(2L, memberEstimateResponse);

		List<ChattingRoomResponse> chattingRoomResponses = List.of(chattingRoomResponse, chattingRoomResponse2);

		ChattingRoomsResponse chattingRoomsResponse = new ChattingRoomsResponse(chattingRoomResponses);

		when(chattingRoomService.findAll()).thenReturn(chattingRoomsResponse);

		//when
		//then
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/chatting-rooms")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.chattingRoomsResponse").isArray())
			.andExpect(jsonPath("$.chattingRoomsResponse[0].id").value(1))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.id").value(1))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.memberId").value(1))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.subItemResponse.id").value(1))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.subItemResponse.mainItemName").value(subItemResponse.mainItemName()))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.subItemResponse.name").value(subItemResponse.name()))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.subItemResponse.description").value(subItemResponse.description()))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.location").value("서울시 강남구"))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.detailedDescription").value("세부 설명"))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.status").value("PROCEEDING"));
	}
//
	@DisplayName("채팅방 단건 조회 성공 테스트")
	@Test
	void findById() throws Exception {
		//given
		MemberEstimateResponse memberEstimateResponse = new MemberEstimateResponse(1L, 1L, 1L, subItemResponse,"서울시 강남구", LocalDateTime.now().plusDays(3), "세부 설명", Status.PROCEEDING);

		Long chattingRoomId = 1L;
		ChattingRoomResponse chattingRoomResponse = new ChattingRoomResponse(chattingRoomId, memberEstimateResponse);

		when(chattingRoomService.findById(chattingRoomId)).thenReturn(chattingRoomResponse);

		//when
		//then
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/chatting-rooms/{chattingRoomId}", chattingRoomId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(chattingRoomId))
			.andExpect(jsonPath("$.memberEstimateResponse.id").value(1))
			.andExpect(jsonPath("$.memberEstimateResponse.memberId").value(1))
			.andExpect(jsonPath("$.memberEstimateResponse.subItemResponse.id").value(1))
			.andExpect(jsonPath("$.memberEstimateResponse.subItemResponse.mainItemName").value(subItemResponse.mainItemName()))
			.andExpect(jsonPath("$.memberEstimateResponse.subItemResponse.name").value(subItemResponse.name()))
			.andExpect(jsonPath("$.memberEstimateResponse.subItemResponse.description").value(subItemResponse.description()))
			.andExpect(jsonPath("$.memberEstimateResponse.location").value("서울시 강남구"))
			.andExpect(jsonPath("$.memberEstimateResponse.detailedDescription").value("세부 설명"))
			.andExpect(jsonPath("$.memberEstimateResponse.status").value("PROCEEDING"));
	}

	@DisplayName("회원 요청 견적서 별 전체 채팅방 조회 성공 테스트")
	@Test
	void findAllByMemberEstimateId() throws Exception {
		//given
		Long memberEstimateId = 2L;

		MemberEstimateResponse memberEstimateResponse = new MemberEstimateResponse(1L, 1L, 1L, subItemResponse,"서울시 강남구", LocalDateTime.now().plusDays(3), "세부 설명", Status.PROCEEDING);

		ChattingRoomResponse chattingRoomResponse = new ChattingRoomResponse(1L, memberEstimateResponse);
		ChattingRoomResponse chattingRoomResponse2 = new ChattingRoomResponse(2L, memberEstimateResponse);

		List<ChattingRoomResponse> chattingRoomResponses = List.of(chattingRoomResponse, chattingRoomResponse2);

		ChattingRoomsResponse chattingRoomsResponse = new ChattingRoomsResponse(chattingRoomResponses);

		when(chattingRoomService.findAllByMemberEstimateId(memberEstimateId)).thenReturn(chattingRoomsResponse);

		//when
		//then
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/chatting-rooms/memberEstimates/{memberEstimateId}", memberEstimateId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.chattingRoomsResponse").isArray())
			.andExpect(jsonPath("$.chattingRoomsResponse[0].id").value(1))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.id").value(1))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.memberId").value(1))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.subItemResponse.id").value(1))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.subItemResponse.mainItemName").value(subItemResponse.mainItemName()))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.subItemResponse.name").value(subItemResponse.name()))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.subItemResponse.description").value(subItemResponse.description()))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.location").value("서울시 강남구"))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.detailedDescription").value("세부 설명"))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.status").value("PROCEEDING"));
	}

	@DisplayName("회원 별 전체 채팅방 조회 성공 테스트")
	@Test
	void findAllByMemberId() throws Exception {
		//given
		Long memberId = 2L;

		MemberEstimateResponse memberEstimateResponse = new MemberEstimateResponse(1L, 1L, 1L, subItemResponse,"서울시 강남구", LocalDateTime.now().plusDays(3), "세부 설명", Status.PROCEEDING);

		ChattingRoomResponse chattingRoomResponse = new ChattingRoomResponse(1L, memberEstimateResponse);
		ChattingRoomResponse chattingRoomResponse2 = new ChattingRoomResponse(2L, memberEstimateResponse);

		List<ChattingRoomResponse> chattingRoomResponses = List.of(chattingRoomResponse, chattingRoomResponse2);

		ChattingRoomsResponse chattingRoomsResponse = new ChattingRoomsResponse(chattingRoomResponses);

		when(chattingRoomService.findAllByMemberId(memberId)).thenReturn(chattingRoomsResponse);

		//when
		//then
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/chatting-rooms/members")
				.param("memberId", String.valueOf(memberId))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.chattingRoomsResponse").isArray())
			.andExpect(jsonPath("$.chattingRoomsResponse[0].id").value(1))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.id").value(1))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.memberId").value(1))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.subItemResponse.id").value(1))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.subItemResponse.mainItemName").value(subItemResponse.mainItemName()))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.subItemResponse.name").value(subItemResponse.name()))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.subItemResponse.description").value(subItemResponse.description()))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.location").value("서울시 강남구"))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.detailedDescription").value("세부 설명"))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.status").value("PROCEEDING"));
	}

	@DisplayName("고수 별 전체 채팅방 조회 성공 테스트")
	@Test
	void findAllByExpertId() throws Exception {
		//given
		Long expertId = 2L;

		MemberEstimateResponse memberEstimateResponse = new MemberEstimateResponse(1L, 1L, 1L, subItemResponse,"서울시 강남구", LocalDateTime.now().plusDays(3), "세부 설명", Status.PROCEEDING);

		ChattingRoomResponse chattingRoomResponse = new ChattingRoomResponse(1L, memberEstimateResponse);
		ChattingRoomResponse chattingRoomResponse2 = new ChattingRoomResponse(2L, memberEstimateResponse);

		List<ChattingRoomResponse> chattingRoomResponses = List.of(chattingRoomResponse, chattingRoomResponse2);

		ChattingRoomsResponse chattingRoomsResponse = new ChattingRoomsResponse(chattingRoomResponses);

		when(chattingRoomService.findAllByExpertId(expertId)).thenReturn(chattingRoomsResponse);

		//when
		//then
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/chatting-rooms/experts")
				.param("expertId", String.valueOf(expertId))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.chattingRoomsResponse").isArray())
			.andExpect(jsonPath("$.chattingRoomsResponse[0].id").value(1))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.id").value(1))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.memberId").value(1))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.subItemResponse.id").value(1))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.subItemResponse.mainItemName").value(subItemResponse.mainItemName()))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.subItemResponse.name").value(subItemResponse.name()))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.subItemResponse.description").value(subItemResponse.description()))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.location").value("서울시 강남구"))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.detailedDescription").value("세부 설명"))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.status").value("PROCEEDING"));
	}

	@DisplayName("고수의 일반 견적 관련 채팅방 목록 조회 성공 테스트")
	@Test
	void findAllOfNormalByExpertId() throws Exception {
		//given
		Long expertId = 2L;

		MemberEstimateResponse memberEstimateResponse = new MemberEstimateResponse(1L, 1L, 1L, subItemResponse,"서울시 강남구", LocalDateTime.now().plusDays(3), "세부 설명", Status.PROCEEDING);

		ChattingRoomResponse chattingRoomResponse = new ChattingRoomResponse(1L, memberEstimateResponse);
		ChattingRoomResponse chattingRoomResponse2 = new ChattingRoomResponse(2L, memberEstimateResponse);

		List<ChattingRoomResponse> chattingRoomResponses = List.of(chattingRoomResponse, chattingRoomResponse2);

		ChattingRoomsResponse chattingRoomsResponse = new ChattingRoomsResponse(chattingRoomResponses);

		when(chattingRoomService.findAllOfNormalByExpertId(expertId)).thenReturn(chattingRoomsResponse);

		//when
		//then
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/chatting-rooms/normal")
				.param("expertId", String.valueOf(expertId))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.chattingRoomsResponse").isArray())
			.andExpect(jsonPath("$.chattingRoomsResponse[0].id").value(1))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.id").value(1))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.memberId").value(1))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.subItemResponse.id").value(1))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.subItemResponse.mainItemName").value(subItemResponse.mainItemName()))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.subItemResponse.name").value(subItemResponse.name()))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.subItemResponse.description").value(subItemResponse.description()))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.location").value("서울시 강남구"))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.detailedDescription").value("세부 설명"))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.status").value("PROCEEDING"));
	}

	@DisplayName("고수의 바로 견적 관련 채팅방 목록 조회 성공 테스트")
	@Test
	void findAllOfAutoByExpertId() throws Exception {
		//given
		Long expertId = 2L;

		MemberEstimateResponse memberEstimateResponse = new MemberEstimateResponse(1L, 1L, 1L, subItemResponse,"서울시 강남구", LocalDateTime.now().plusDays(3), "세부 설명", Status.PROCEEDING);

		ChattingRoomResponse chattingRoomResponse = new ChattingRoomResponse(1L, memberEstimateResponse);
		ChattingRoomResponse chattingRoomResponse2 = new ChattingRoomResponse(2L, memberEstimateResponse);

		List<ChattingRoomResponse> chattingRoomResponses = List.of(chattingRoomResponse, chattingRoomResponse2);

		ChattingRoomsResponse chattingRoomsResponse = new ChattingRoomsResponse(chattingRoomResponses);

		when(chattingRoomService.findAllOfAutoByExpertId(expertId)).thenReturn(chattingRoomsResponse);

		//when
		//then
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/chatting-rooms/auto")
				.param("expertId", String.valueOf(expertId))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.chattingRoomsResponse").isArray())
			.andExpect(jsonPath("$.chattingRoomsResponse[0].id").value(1))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.id").value(1))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.memberId").value(1))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.subItemResponse.id").value(1))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.subItemResponse.mainItemName").value(subItemResponse.mainItemName()))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.subItemResponse.name").value(subItemResponse.name()))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.subItemResponse.description").value(subItemResponse.description()))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.location").value("서울시 강남구"))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.detailedDescription").value("세부 설명"))
			.andExpect(jsonPath("$.chattingRoomsResponse[0].memberEstimateResponse.status").value("PROCEEDING"));
	}

	@DisplayName("채팅방 삭제 성공 테스트")
	@Test
	void delete() throws Exception {
		//given
		Long chattingRoomId = 1L;

		doNothing().when(chattingRoomService).delete(chattingRoomId);

		//when
		//then
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/chatting-rooms/{id}", chattingRoomId)
			.contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk());
	}

	@DisplayName("채팅방 삭제 실패 테스트")
	@Test
	void deleteFailed() throws Exception {
		//given
		doThrow(new EntityNotFoundException(ErrorCode.NOT_FOUND_CHATTING_ROOM)).when(
			chattingRoomService).delete(any(Long.class));

		//when
		//then
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/chatting-rooms/{id}", 1L)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("CR001"))
			.andExpect(jsonPath("$.errors").isArray())
			.andExpect(jsonPath("$.message").value("채팅방이 존재하지 않습니다."));
	}
}
