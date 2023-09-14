package com.foo.gosucatcher.domain.search.presentation;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.foo.gosucatcher.domain.item.application.dto.response.sub.SubItemResponse;
import com.foo.gosucatcher.domain.item.application.dto.response.sub.SubItemsResponse;
import com.foo.gosucatcher.domain.search.application.SearchService;
import com.foo.gosucatcher.domain.search.application.dto.response.SearchListResponse;
import com.foo.gosucatcher.domain.search.application.dto.response.SearchResponse;

@WebMvcTest(value = {SearchController.class}, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class SearchControllerTest {


	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private SearchService searchService;

	@DisplayName("키워드로 검색시 연관 서비스 조회 성공")
	@Test
	void searchKeywordTest() throws Exception {
		//given
		long memberId = 1L;
		String keyword = "알바";

		SubItemsResponse subItemsResponse = new SubItemsResponse(
			List.of(new SubItemResponse(1L, "알바", "청소 알바", "청소 알바 설명")));

		Mockito.when(searchService.searchKeyword(memberId, keyword)).thenReturn(subItemsResponse);

		//when -> then
		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/search")
				.param("keyword", keyword)
				.param("memberId", String.valueOf(memberId))
				.contentType(APPLICATION_JSON)
				.accept(APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.subItemsResponse[0].id").value(1L))
			.andExpect(jsonPath("$.subItemsResponse[0].mainItemName").value("알바"))
			.andDo(print());
	}

	@DisplayName("최근 검색 목록 반환 성공")
	@Test
	void getSearchListTest() throws Exception {
		//given
		long memberId = 1L;

		SearchListResponse searchListResponse = new SearchListResponse(
			List.of(new SearchResponse("영어", LocalDate.now()))
		);

		Mockito.when(searchService.getResentSearchList(memberId)).thenReturn(searchListResponse);

		// Act and Assert
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/search")
				.param("memberId", String.valueOf(memberId))
				.contentType(APPLICATION_JSON)
				.accept(APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.searchResponseList[0].keyword").value("영어"))
			.andDo(print());
	}
}
