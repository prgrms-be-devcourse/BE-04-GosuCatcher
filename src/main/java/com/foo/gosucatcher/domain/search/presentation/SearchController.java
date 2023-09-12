package com.foo.gosucatcher.domain.search.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foo.gosucatcher.domain.search.application.SearchService;
import com.foo.gosucatcher.domain.search.application.dto.response.SearchListResponse;
import com.foo.gosucatcher.domain.search.application.dto.response.SearchResponse;
import com.foo.gosucatcher.global.aop.CurrentMemberId;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {

	private final SearchService searchService;

	@PostMapping
	@CurrentMemberId
	public ResponseEntity<SearchResponse> search(@RequestParam String keyword, Long memberId) {
		SearchResponse searchResponse = searchService.searchKeyword(memberId, keyword);

		return ResponseEntity.ok(searchResponse);
	}

	@GetMapping
	@CurrentMemberId
	public ResponseEntity<SearchListResponse> getSearchList(Long memberId) {
		SearchListResponse searchList = searchService.getSearchList(memberId);

		return ResponseEntity.ok(searchList);
	}
}
