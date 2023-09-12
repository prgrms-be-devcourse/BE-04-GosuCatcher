package com.foo.gosucatcher.domain.search.application;

import java.util.List;

import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.domain.member.domain.MemberRepository;
import com.foo.gosucatcher.domain.search.application.dto.response.SearchListResponse;
import com.foo.gosucatcher.domain.search.application.dto.response.SearchResponse;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SearchService {

	private final RedisTemplate<String, String> redisTemplate;
	private final MemberRepository memberRepository;

	public SearchResponse searchKeyword(Long memberId, String keyword) {
		if (keyword == null || keyword.isBlank() || keyword.isEmpty()) return null;

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));
		String key = "search::" + member.getId();

		ListOperations<String, String> listOperations = redisTemplate.opsForList();
		for (String pastKeyword : listOperations.range(key, 0, listOperations.size(key))) {
			if (String.valueOf(pastKeyword).equals(keyword)) return null;
		}

		if (listOperations.size(key) < 5) {
			listOperations.rightPush(key, keyword);
		} else if (listOperations.size(key) == 5) {
			listOperations.leftPop(key);
			listOperations.rightPush(key, keyword);
		}

		return SearchResponse.from(keyword);
	}

	@Transactional(readOnly = true)
	public SearchListResponse getSearchList(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));
		String key = "search::" + member.getId();

		ListOperations<String, String> listOperations = redisTemplate.opsForList();


		List<String> range = listOperations.range(key, 0, listOperations.size(key));

		List<SearchResponse> searchResponses = range.stream()
			.map(SearchResponse::from)
			.toList();

		return SearchListResponse.from(searchResponses);
	}
}
