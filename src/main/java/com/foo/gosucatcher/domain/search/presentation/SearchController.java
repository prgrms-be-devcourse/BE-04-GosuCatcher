package com.foo.gosucatcher.domain.search.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foo.gosucatcher.domain.item.application.dto.response.sub.SubItemsResponse;
import com.foo.gosucatcher.domain.search.application.SearchService;
import com.foo.gosucatcher.domain.search.application.dto.response.SearchListResponse;
import com.foo.gosucatcher.domain.search.application.dto.response.SearchRankingListResponse;
import com.foo.gosucatcher.global.aop.CurrentMemberId;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "SearchController", description = "검색/최근 검색어 조회/인기 검색어 조회")
@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {

	private final SearchService searchService;

	@Operation(summary = "검색어 입력", description = "검색어 입력시, 최근 검색어/인기 검색어에 카운팅 됩니다.")
	@PostMapping
	@CurrentMemberId
	public ResponseEntity<SubItemsResponse> search(@Parameter(description = "검색 키워드", required = true, example = "개발") @RequestParam String keyword,
												   @Parameter(description = "로그인한 유저의 ID", required = true, example = "1") Long memberId) {
		SubItemsResponse subItemsResponse = searchService.searchKeyword(memberId, keyword);

		return ResponseEntity.ok(subItemsResponse);
	}

	@Operation(summary = "로그인한 유저의 최근 검색어 조회", description = "로그인한 해당 유저의 최근 검색어가 조회 됩니다.")
	@GetMapping
	@CurrentMemberId
	public ResponseEntity<SearchListResponse> getRecentSearchList(@Parameter(description = "로그인 멤버 id", required = true) Long memberId) {
		SearchListResponse searchList = searchService.getResentSearchList(memberId);

		return ResponseEntity.ok(searchList);
	}

	@Operation(summary = "사이트내 인기 검색어 조회",description = "사이트내 인기 검색어가 조회 됩니다.")
	@GetMapping("/popularity")
	public ResponseEntity<SearchRankingListResponse> getPopularSearchList() {
		SearchRankingListResponse popularKeywords = searchService.getPopularKeywords();

		return ResponseEntity.ok(popularKeywords);
	}
}
