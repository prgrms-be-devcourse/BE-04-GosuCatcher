package com.foo.gosucatcher.domain.item.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foo.gosucatcher.domain.item.application.SubItemService;
import com.foo.gosucatcher.domain.item.application.dto.request.SubItemCreateRequest;
import com.foo.gosucatcher.domain.item.application.dto.request.SubItemUpdateRequest;
import com.foo.gosucatcher.domain.item.application.dto.response.SubItemResponse;
import com.foo.gosucatcher.domain.item.application.dto.response.SubItemsResponse;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.BusinessException;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;

@WebMvcTest(SubItemController.class)
class SubItemControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	SubItemService subItemService;

	private SubItemCreateRequest subItemCreateRequest;

	@BeforeEach
	void setUp() {
		subItemCreateRequest = new SubItemCreateRequest(1L, "청소 알바", "청소 설명 입니다.");
	}

	@Test
	@DisplayName("하위 서비스 등록 성공")
	void createSubItemSuccessTest() throws Exception {

		//given
		SubItemResponse subItemResponse = new SubItemResponse(1L, "알바", "청소 알바", "청소 설명 입니다.");

		given(subItemService.create(any())).willReturn(subItemResponse);

		//when -> then
		mockMvc.perform(post("/api/v1/sub-items")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(subItemCreateRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.mainItemName").value("알바"))
			.andExpect(jsonPath("$.name").value("청소 알바"))
			.andExpect(jsonPath("$.description").value("청소 설명 입니다."))
			.andDo(print());
	}

	@Test
	@DisplayName("하위 서비스 등록 실패 - 존재하지 않는 메인 서비스")
	void createSubItemFailTest_notFoundMainItem() throws Exception {

		//given
		given(subItemService.create(any()))
			.willThrow(new EntityNotFoundException(ErrorCode.NOT_FOUND_MAIN_ITEM));

		//when -> then
		mockMvc.perform(post("/api/v1/sub-items")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(subItemCreateRequest)))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("MI001"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("메인 서비스를 찾을 수 없습니다."))
			.andDo(print());
	}

	@Test
	@DisplayName("하위 서비스 등록 실패 - 중복된 하위 서비스 명")
	void createSubItemFailTest_duplicatedName() throws Exception {

		//given
		SubItemCreateRequest duplicatedSubItemCreateRequest = new SubItemCreateRequest(1L, "청소 알바", "설명을적습니다.");

		given(subItemService.create(any()))
			.willThrow(new BusinessException(ErrorCode.DUPLICATED_SUB_ITEM_NAME));

		//when -> then
		mockMvc.perform(post("/api/v1/sub-items")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(duplicatedSubItemCreateRequest)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("SI002"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("하위 서비스 이름이 중복될 수 없습니다."))
			.andDo(print());
	}

	@Test
	@DisplayName("메인 서비스 등록 실패 - 잘못된 값을 입력")
	void createSubItemFailTest_invalidValue() throws Exception {

		//given
		SubItemCreateRequest invalidSubItemCreateRequest = new SubItemCreateRequest(1L, "@#@#", "청소 설명 입니다.");

		//when -> then
		mockMvc.perform(post("/api/v1/sub-items")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidSubItemCreateRequest)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("C001"))
			.andExpect(jsonPath("$.errors[0].value").value("@#@#"))
			.andExpect(jsonPath("$.errors[0].reason").value("하위 서비스명은 한글과 숫자만 입력 가능합니다."))
			.andExpect(jsonPath("$.message").value("잘못된 값을 입력하셨습니다."))
			.andDo(print());
	}

	@Test
	@DisplayName("하위 서비스 전체 조회")
	void findAllSuccessTest() throws Exception {

		//givenR
		SubItemsResponse subItemsResponse = new SubItemsResponse(
			List.of(new SubItemResponse(1L, "알바", "청소 알바", "청소 알바 설명")));
		given(subItemService.findAll()).willReturn(subItemsResponse);

		//when -> then
		mockMvc.perform(get("/api/v1/sub-items"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.subItemsResponse[0].id").value(1L))
			.andExpect(jsonPath("$.subItemsResponse[0].mainItemName").value("알바"))
			.andExpect(jsonPath("$.subItemsResponse[0].name").value("청소 알바"))
			.andDo(print());
	}

	@Test
	@DisplayName("하위 서비스 ID로 조회")
	void findSubItemByIdSuccessTest() throws Exception {

		//given
		SubItemResponse subItemResponse = new SubItemResponse(1L, "알바", "청소 알바", "설명");
		given(subItemService.findById(anyLong())).willReturn(subItemResponse);

		//when -> then
		mockMvc.perform(get("/api/v1/sub-items/{id}", 1L))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.mainItemName").value("알바"))
			.andExpect(jsonPath("$.name").value("청소 알바"))
			.andExpect(jsonPath("$.description").value("설명"))
			.andDo(print());
	}

	@Test
	@DisplayName("하위 서비스 ID로 조회 실패 - 존재하지 않는 하위 서비스")
	void findSubItemByIdFailTest_notFoundSubItem() throws Exception {

		//given
		Long invalidId = 100L;
		given(subItemService.findById(anyLong()))
			.willThrow(new EntityNotFoundException(ErrorCode.DUPLICATED_SUB_ITEM_NAME));

		//when -> then
		mockMvc.perform(get("/api/v1/sub-items/{id}", invalidId))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("SI002"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("하위 서비스 이름이 중복될 수 없습니다."))
			.andDo(print());
	}

	@Test
	@DisplayName("하위 서비스 수정 성공")
	void updateSubItemSuccessTest() throws Exception {

		//given
		SubItemUpdateRequest subItemUpdateRequest = new SubItemUpdateRequest("축구 레슨", "설명을적습니다.");
		SubItemResponse subItemResponse = new SubItemResponse(1L, "레슨", "축구 레슨", "수정된설명을적습니다.");

		given(subItemService.update(anyLong(), any()))
			.willReturn(subItemResponse.id());

		//when -> then
		mockMvc.perform(patch("/api/v1/sub-items/{id}", 1L, subItemUpdateRequest)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(subItemUpdateRequest)))
			.andExpect(status().isOk())
			.andDo(print());
	}
}
