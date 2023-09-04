package com.foo.gosucatcher.domain.likes.presentation;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import com.foo.gosucatcher.domain.likes.application.LikesService;
import com.foo.gosucatcher.domain.likes.dto.request.LikesRequest;
import com.foo.gosucatcher.domain.likes.dto.response.LikesResponse;
import com.foo.gosucatcher.domain.likes.dto.response.LikesResponses;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;

@WebMvcTest(LikesController.class)
class LikesControllerTest {

	String apiBaseUrl = "/api/v1/likes";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private LikesService likesService;

	@Test
	@DisplayName("찜 내역을 모두 조회할 수 있다")
	void findAll() throws Exception {
		// given
		LikesResponses likesResponses = new LikesResponses(List.of(new LikesResponse(1L, 2L, 3L)));
		given(likesService.findAll())
				.willReturn(likesResponses);

		// when
		// then
		mockMvc.perform(get(apiBaseUrl)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.likesResponses[0].id").value(1L))
				.andExpect(jsonPath("$.likesResponses[0].expertId").value(2L))
				.andExpect(jsonPath("$.likesResponses[0].memberId").value(3L));
	}

	@Test
	@DisplayName("사용자는 고수를 찜할 수 있다")
	void like() throws Exception {

		// given
		LikesRequest likesRequest = new LikesRequest(1L, 2L);
		LikesResponse likesResponse = new LikesResponse(0L, 1L, 2L);
		given(likesService.create(any(LikesRequest.class)))
				.willReturn(likesResponse);

		// when
		// then
		mockMvc.perform(MockMvcRequestBuilders.post(apiBaseUrl)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(likesRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(0L))
				.andExpect(jsonPath("$.expertId").value(1L))
				.andExpect(jsonPath("$.memberId").value(2L));
	}

	@Test
	@DisplayName("사용자가 특정 고수를 찜했는지 여부를 조회할 수 있다")
	void checkLikedYN() throws Exception {

		// given
		LikesRequest likesRequest = new LikesRequest(1L, 2L);
		likesService.create(likesRequest);

		LikesResponse likesResponse = new LikesResponse(0L, 1L, 2L);
		given(likesService.checkStatus(any(LikesRequest.class)))
				.willReturn(Boolean.TRUE);

		// when
		// then
		mockMvc.perform(MockMvcRequestBuilders.post(apiBaseUrl + "/status")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(likesRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value("true"));
	}

	@Test
	@DisplayName("사용자는 찜을 취소할 수 있다")
	void delete() throws Exception {

		// given
		long id = 0L;
		doNothing()
				.when(likesService)
				.deleteById(id);

		// when
		// then
		mockMvc.perform(MockMvcRequestBuilders.delete(apiBaseUrl + "/" + id)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk());

		doThrow(new EntityNotFoundException(ErrorCode.NOT_FOUND_LIKES)).
				when(likesService)
				.deleteById(id);
	}
}
