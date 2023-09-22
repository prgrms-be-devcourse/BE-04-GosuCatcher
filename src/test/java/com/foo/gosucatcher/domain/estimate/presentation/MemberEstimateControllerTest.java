package com.foo.gosucatcher.domain.estimate.presentation;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.NULL;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foo.gosucatcher.domain.chat.application.dto.response.ChattingRoomResponse;
import com.foo.gosucatcher.domain.chat.application.dto.response.MessageResponse;
import com.foo.gosucatcher.domain.chat.application.dto.response.MessagesResponse;
import com.foo.gosucatcher.domain.estimate.application.MemberEstimateService;
import com.foo.gosucatcher.domain.estimate.application.dto.request.MemberEstimateRequest;
import com.foo.gosucatcher.domain.estimate.application.dto.response.MemberEstimateResponse;
import com.foo.gosucatcher.domain.estimate.application.dto.response.MemberEstimatesResponse;
import com.foo.gosucatcher.domain.estimate.domain.MemberEstimate;
import com.foo.gosucatcher.domain.estimate.domain.Status;
import com.foo.gosucatcher.domain.expert.application.dto.response.ExpertResponse;
import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.item.application.dto.response.sub.SubItemResponse;
import com.foo.gosucatcher.domain.item.domain.MainItem;
import com.foo.gosucatcher.domain.item.domain.SubItem;
import com.foo.gosucatcher.domain.matching.application.MatchingService;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;

@WebMvcTest(value = {MemberEstimateController.class}, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@AutoConfigureRestDocs
class MemberEstimateControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private MemberEstimateService memberEstimateService;

	@MockBean
	private MatchingService matchingService;

	private Member member;
	private MainItem mainItem;
	private SubItem subItem;
	private MemberEstimate memberEstimate;
	private Expert expert;

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

		memberEstimate = MemberEstimate.builder()
			.member(member)
			.subItem(subItem)
			.location("서울 강남구 개포1동")
			.preferredStartDate(LocalDateTime.now().plusDays(3))
			.detailedDescription("추가 내용")
			.build();

		expert = Expert.builder()
			.location("서울시 강남구")
			.rating(4.0)
			.maxTravelDistance(10)
			.reviewCount(5)
			.storeName("업체명")
			.description("추가 설명입니다")
			.member(member)
			.build();
	}

	@DisplayName("회원 일반 견적 등록 성공 테스트")
	@Test
	void createNormal() throws Exception {
		//given
		Long memberId = 1L;
		Long subItemId = 1L;
		Long expertId = 1L;

		SubItemResponse subItemResponse = SubItemResponse.from(subItem);

		MemberEstimateRequest memberEstimateRequest = new MemberEstimateRequest(subItemId,
			"서울 강남구 개포1동", LocalDateTime.now().plusDays(3), "추가 내용");

		MemberEstimateResponse memberEstimateResponse = new MemberEstimateResponse(1L, memberId,
			expertId, subItemResponse, "서울 강남구 개포1동", LocalDateTime.now().plusDays(4), "추가 내용", Status.PENDING);

		when(memberEstimateService.createNormal(anyLong(), anyLong(), any(MemberEstimateRequest.class))).thenReturn(memberEstimateResponse);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/member-estimates/normal/{expertId}", expertId)
				.content(objectMapper.writeValueAsString(memberEstimateRequest))
				.param("memberId", String.valueOf(memberId))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.subItemResponse.mainItemName").value(subItem.getMainItem().getName()))
			.andExpect(jsonPath("$.subItemResponse.name").value(subItem.getName()))
			.andExpect(jsonPath("$.subItemResponse.description").value(subItem.getDescription()))
			.andExpect(jsonPath("$.location").value("서울 강남구 개포1동"))
			.andExpect(jsonPath("$.detailedDescription").value("추가 내용"))
			.andExpect(jsonPath("$.status").value("PENDING"))
			.andDo(document("create-normal-estimate",
				Preprocessors.preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(
					parameterWithName("expertId").description("고수 ID")
				),
				requestFields(
					fieldWithPath("subItemId").type(NUMBER).description("세부 서비스 ID"),
					fieldWithPath("location").type(STRING).description("서비스 희망 지역"),
					fieldWithPath("preferredStartDate").type(STRING).description("서비스 희망 날짜"),
					fieldWithPath("detailedDescription").type(STRING).description("상세 설명")
				),
				responseFields(
					fieldWithPath("id").type(NUMBER).description("고객 일반 견적서 ID"),
					fieldWithPath("memberId").type(NUMBER).description("견적을 요청한 회원 ID"),
					fieldWithPath("expertId").type(NUMBER).description("고수 ID"),
					fieldWithPath("subItemResponse.id").type(NULL).description("세부 서비스 ID"),
					fieldWithPath("subItemResponse.mainItemName").type(STRING).description("메인 서비스 ID"),
					fieldWithPath("subItemResponse.name").type(STRING).description("세부 서비스 이름"),
					fieldWithPath("subItemResponse.description").type(STRING).description("세부 서비스 설명"),
					fieldWithPath("location").type(STRING).description("서비스 희망 지역"),
					fieldWithPath("detailedDescription").type(STRING).description("상세 설명"),
					fieldWithPath("status").type(STRING).description("견적서 요청 상태"),
					fieldWithPath("preferredStartDate").type(STRING).description("서비스 희망 날짜")
				)
			));
	}

	@DisplayName("회원 일반 견적 등록 실패 테스트")
	@Test
	void createNormalFailed() throws Exception {
		//given
		Long memberId = 1L;
		Long expertId = 1L;

		MemberEstimateRequest memberEstimateRequest = new MemberEstimateRequest(null,
			"서울 강남구 개포1동", LocalDateTime.now().plusDays(3), "추가 내용");

		MemberEstimateResponse memberEstimateResponse = new MemberEstimateResponse(1L, memberId,
			expertId, null, "서울 강남구 개포1동", LocalDateTime.now().plusDays(4), "추가 내용", Status.PENDING);

		when(memberEstimateService.createNormal(anyLong(), anyLong(), any(MemberEstimateRequest.class))).thenReturn(memberEstimateResponse);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/member-estimates/normal/{expertId}", expertId)
				.content(objectMapper.writeValueAsString(memberEstimateRequest))
				.param("memberId", String.valueOf(memberId))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("C001"))
			.andExpect(jsonPath("$.errors").isArray())
			.andExpect(jsonPath("$.errors[0].field").value("subItemId"))
			.andExpect(jsonPath("$.errors[0].value").value(""))
			.andExpect(jsonPath("$.errors[0].reason").value("세부 서비스 id를 등록해주세요."))
			.andExpect(jsonPath("$.message").value("잘못된 값을 입력하셨습니다."))
			.andDo(document("create-normal-estimate-fail-invalid-subItemId",
				Preprocessors.preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(
					parameterWithName("expertId").description("고수 ID")
				),
				requestFields(
					fieldWithPath("subItemId").type(NULL).description("세부 서비스 ID"),
					fieldWithPath("location").type(STRING).description("서비스 희망 지역"),
					fieldWithPath("preferredStartDate").type(STRING).description("서비스 희망 날짜"),
					fieldWithPath("detailedDescription").type(STRING).description("상세 설명")
				),
				responseFields(
					fieldWithPath("code").type(STRING).description("에러 코드"),
					fieldWithPath("errors[0].field").type(STRING).description("에러 필드"),
					fieldWithPath("errors[0].value").type(STRING).description("에러 값"),
					fieldWithPath("errors[0].reason").type(STRING).description("에러 원인"),
					fieldWithPath("message").type(STRING).description("에러 메시지"),
					fieldWithPath("timestamp").type(STRING).description("에러 시간")
				)
			));
	}

	@DisplayName("회원 바로 견적 등록 성공 테스트")
	@Test
	void createAuto() throws Exception {
		//given
		Long memberId = 1L;
		Long subItemId = 1L;
		Long expertId = 1L;

		SubItemResponse subItemResponse = new SubItemResponse(1L, subItem.getMainItem().getName(), subItem.getName(), subItem.getDescription());

		MemberEstimateRequest memberEstimateRequest = new MemberEstimateRequest(subItemId,
			"서울 강남구 개포1동", LocalDateTime.now().plusDays(3), "추가 내용");

		MemberEstimateResponse memberEstimateResponse = new MemberEstimateResponse(1L, memberId,
			expertId, subItemResponse, "서울 강남구 개포1동", LocalDateTime.now().plusDays(4), "추가 내용", Status.PROCEEDING);

		ExpertResponse expertResponse = new ExpertResponse(2L, "업체명", "서울 강남구", 10, "expert description", 4.0, 6,null);

		ChattingRoomResponse chattingRoomResponse = new ChattingRoomResponse(1L, memberEstimateResponse);
		MessageResponse messageResponse = new MessageResponse(1L, expertResponse.id(), chattingRoomResponse, "고수 견적서 내용입니다.");

		when(memberEstimateService.createAuto(anyLong(), any(MemberEstimateRequest.class))).thenReturn(memberEstimateResponse);
		when(matchingService.match(any(MemberEstimateResponse.class))).thenReturn(new MessagesResponse(List.of(messageResponse)));

		//when
		//then
		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/member-estimates/auto")
				.content(objectMapper.writeValueAsString(memberEstimateRequest))
				.param("memberId", String.valueOf(memberId))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.messagesResponse").isArray())
			.andExpect(jsonPath("$.messagesResponse[0].id").value(1L))
			.andExpect(jsonPath("$.messagesResponse[0].senderId").value(expertResponse.id()))
			.andExpect(jsonPath("$.messagesResponse[0].chattingRoomResponse.id").value(1L))
			.andExpect(jsonPath("$.messagesResponse[0].chattingRoomResponse.memberEstimateResponse.id").value(memberEstimateResponse.id()))
			.andExpect(jsonPath("$.messagesResponse[0].chattingRoomResponse.memberEstimateResponse.memberId").value(memberId))
			.andExpect(jsonPath("$.messagesResponse[0].chattingRoomResponse.memberEstimateResponse.subItemResponse.id").value(1L))
			.andExpect(jsonPath("$.messagesResponse[0].chattingRoomResponse.memberEstimateResponse.subItemResponse.mainItemName").value(subItemResponse.mainItemName()))
			.andExpect(jsonPath("$.messagesResponse[0].chattingRoomResponse.memberEstimateResponse.subItemResponse.name").value(subItemResponse.name()))
			.andExpect(jsonPath("$.messagesResponse[0].chattingRoomResponse.memberEstimateResponse.subItemResponse.description").value(subItemResponse.description()))
			.andExpect(jsonPath("$.messagesResponse[0].chattingRoomResponse.memberEstimateResponse.location").value("서울 강남구 개포1동"))
			.andExpect(jsonPath("$.messagesResponse[0].chattingRoomResponse.memberEstimateResponse.detailedDescription").value("추가 내용"))
			.andExpect(jsonPath("$.messagesResponse[0].chattingRoomResponse.memberEstimateResponse.status").value("PROCEEDING"))
			.andExpect(jsonPath("$.messagesResponse[0].content").value("고수 견적서 내용입니다."));
	}

	@DisplayName("회원 바로 견적 등록 실패 테스트")
	@Test
	void createAutoFailed() throws Exception {
		//given
		Long memberId = 1L;
		Long subItemId = 1L;
		Long expertId = 1L;

		SubItemResponse subItemResponse = SubItemResponse.from(subItem);

		MemberEstimateRequest memberEstimateRequest = new MemberEstimateRequest(subItemId, " ",
			LocalDateTime.now().plusDays(3), "추가 내용");

		MemberEstimateResponse memberEstimateResponse = new MemberEstimateResponse(1L, memberId,
			expertId, subItemResponse, " ", LocalDateTime.now().plusDays(3), "추가 내용", Status.PROCEEDING);

		when(memberEstimateService.createAuto(memberId, memberEstimateRequest)).thenReturn(
			memberEstimateResponse);

		//when
		//then
		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/member-estimates/auto")
				.content(objectMapper.writeValueAsString(memberEstimateRequest))
				.param("memberId", String.valueOf(memberId))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("C001"))
			.andExpect(jsonPath("$.errors").isArray())
			.andExpect(jsonPath("$.errors[0].field").value("location"))
			.andExpect(jsonPath("$.errors[0].value").value(" "))
			.andExpect(jsonPath("$.errors[0].reason").value("지역을 등록해주세요."))
			.andExpect(jsonPath("$.message").value("잘못된 값을 입력하셨습니다."));
	}

	@DisplayName("전체 견적서 목록 조회 성공 테스트")
	@Test
	void findAll() throws Exception {
		//given
		Member member = Member.builder()
			.name("성이름")
			.password("abcd11@@")
			.email("abcd123@abc.com")
			.phoneNumber("010-0000-0000")
			.build();

		MainItem mainItem = MainItem.builder().name("메인 서비스 이름").description("메인 서비스 설명").build();

		SubItem subItem = SubItem.builder().mainItem(mainItem).name("세부 서비스 이름").description("세부 서비스 설명").build();

		MemberEstimate memberEstimate = MemberEstimate.builder()
			.member(member)
			.subItem(subItem)
			.location("서울 강남구 개포1동")
			.preferredStartDate(LocalDateTime.now().plusDays(3))
			.detailedDescription("추가 내용")
			.build();

		List<MemberEstimate> mockEstimates = List.of(memberEstimate);
		MemberEstimatesResponse memberEstimatesResponse = MemberEstimatesResponse.from(mockEstimates);

		when(memberEstimateService.findAll()).thenReturn(memberEstimatesResponse);

		//when
		//then
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/member-estimates")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.memberEstimates").isArray())
			.andExpect(jsonPath("$.memberEstimates[0].subItemResponse.mainItemName").value(subItem.getMainItem().getName()))
			.andExpect(jsonPath("$.memberEstimates[0].subItemResponse.name").value(subItem.getName()))
			.andExpect(jsonPath("$.memberEstimates[0].subItemResponse.description").value(subItem.getDescription()))
			.andExpect(jsonPath("$.memberEstimates[0].location").value("서울 강남구 개포1동"))
			.andExpect(jsonPath("$.memberEstimates[0].detailedDescription").value("추가 내용"))
			.andExpect(jsonPath("$.memberEstimates[0].status").value("PENDING"));
	}

	@DisplayName("회원 별 전체 요청 견적서 조회 성공 테스트")
	@Test
	void findAllByMemberId() throws Exception {
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

		MemberEstimate memberEstimate = MemberEstimate.builder()
			.member(member)
			.subItem(subItem)
			.location("서울 강남구 개포1동")
			.preferredStartDate(LocalDateTime.now().plusDays(3))
			.detailedDescription("추가 내용")
			.build();

		List<MemberEstimate> mockEstimates = List.of(memberEstimate);
		MemberEstimatesResponse memberEstimatesResponse = MemberEstimatesResponse.from(mockEstimates);

		when(memberEstimateService.findAllByMemberId(memberId)).thenReturn(memberEstimatesResponse);

		//when
		//then
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/member-estimates/members")
				.param("memberId", String.valueOf(memberId))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.memberEstimates").isArray())
			.andExpect(jsonPath("$.memberEstimates[0].subItemResponse.mainItemName").value(subItem.getMainItem().getName()))
			.andExpect(jsonPath("$.memberEstimates[0].subItemResponse.name").value(subItem.getName()))
			.andExpect(jsonPath("$.memberEstimates[0].subItemResponse.description").value(subItem.getDescription()))
			.andExpect(jsonPath("$.memberEstimates[0].location").value("서울 강남구 개포1동"))
			.andExpect(jsonPath("$.memberEstimates[0].detailedDescription").value("추가 내용"))
			.andExpect(jsonPath("$.memberEstimates[0].status").value("PENDING"));
	}

	@DisplayName("회원 요청 견적서 단건 조회 성공 테스트")
	@Test
	void findById() throws Exception {
		//given
		Long memberEstimateId = 1L;

		Long memberId = 1L;
		Long expertId = 1L;

		SubItemResponse subItemResponse = new SubItemResponse(1L, subItem.getMainItem().getName(), subItem.getName(), subItem.getDescription());

		MemberEstimateResponse memberEstimateResponse = new MemberEstimateResponse(1L, memberId,
			expertId, subItemResponse, "서울 강남구 개포1동", LocalDateTime.now().plusDays(3), "추가 내용", Status.PROCEEDING);

		when(memberEstimateService.findById(memberEstimateId)).thenReturn(memberEstimateResponse);

		//when
		//then
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/member-estimates/{memberEstimateId}",
				memberEstimateId).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(memberEstimateId))
			.andExpect(jsonPath("$.memberId").value(memberId))
			.andExpect(jsonPath("$.subItemResponse.id").value(1L))
			.andExpect(jsonPath("$.subItemResponse.mainItemName").value(subItem.getMainItem().getName()))
			.andExpect(jsonPath("$.subItemResponse.name").value(subItem.getName()))
			.andExpect(jsonPath("$.subItemResponse.description").value(subItem.getDescription()))
			.andExpect(jsonPath("$.location").value("서울 강남구 개포1동"))
			.andExpect(jsonPath("$.detailedDescription").value("추가 내용"))
			.andExpect(jsonPath("$.status").value("PROCEEDING"));
	}

	@DisplayName("회원 요청 견적서 단건 조회 실패 테스트")
	@Test
	void findByIdFailed() throws Exception {
		//given
		when(memberEstimateService.findById(any(Long.class))).thenThrow(
			new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER_ESTIMATE));

		//when
		//then
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/member-estimates/{memberEstimateId}", 1L)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("ME001"))
			.andExpect(jsonPath("$.errors").isArray())
			.andExpect(jsonPath("$.message").value("존재하지 않는 회원 요청 견적서입니다."));
	}

	@DisplayName("고수의 응답을 받기까지 대기중인 고수 별 일반 요청 견적서 목록 조회 성공 테스트")
	@Test
	void findAllPendingNormalByExpertId() throws Exception {
		//given
		Long expertId = 1L;

		Member member = Member.builder()
			.name("성이름")
			.password("abcd11@@")
			.email("abcd123@abc.com")
			.phoneNumber("010-0000-0000")
			.build();

		MainItem mainItem = MainItem.builder().name("메인 서비스 이름").description("메인 서비스 설명").build();

		SubItem subItem = SubItem.builder().mainItem(mainItem).name("세부 서비스 이름").description("세부 서비스 설명").build();

		MemberEstimate memberEstimate = MemberEstimate.builder()
			.member(member)
			.subItem(subItem)
			.location("서울 강남구 개포1동")
			.preferredStartDate(LocalDateTime.now().plusDays(3))
			.detailedDescription("추가 내용")
			.build();

		List<MemberEstimate> mockEstimates = List.of(memberEstimate);
		MemberEstimatesResponse memberEstimatesResponse = MemberEstimatesResponse.from(mockEstimates);

		when(memberEstimateService.findAllPendingNormalByExpertId(expertId)).thenReturn(memberEstimatesResponse);

		//when
		//then
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/member-estimates/normal")
				.param("expertId", String.valueOf(expertId))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.memberEstimates").isArray())
			.andExpect(jsonPath("$.memberEstimates[0].subItemResponse.mainItemName").value(subItem.getMainItem().getName()))
			.andExpect(jsonPath("$.memberEstimates[0].subItemResponse.name").value(subItem.getName()))
			.andExpect(jsonPath("$.memberEstimates[0].subItemResponse.description").value(subItem.getDescription()))
			.andExpect(jsonPath("$.memberEstimates[0].location").value("서울 강남구 개포1동"))
			.andExpect(jsonPath("$.memberEstimates[0].detailedDescription").value("추가 내용"))
			.andExpect(jsonPath("$.memberEstimates[0].status").value("PENDING"));
	}

	@DisplayName("회원 요청 견적서 삭제 성공 테스트")
	@Test
	void delete() throws Exception {
		//given
		Long memberEstimateId = 1L;

		doNothing().when(memberEstimateService).delete(memberEstimateId);

		//when
		//then
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/member-estimates/{id}", memberEstimateId)
			.contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk());
	}

	@DisplayName("회원 요청 견적서 삭제 실패 테스트")
	@Test
	void deleteFailed() throws Exception {
		//given
		doThrow(new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER_ESTIMATE)).when(
			memberEstimateService).delete(any(Long.class));

		//when
		//then
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/member-estimates/{id}", 1L)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("ME001"))
			.andExpect(jsonPath("$.errors").isArray())
			.andExpect(jsonPath("$.message").value("존재하지 않는 회원 요청 견적서입니다."));
	}
}
