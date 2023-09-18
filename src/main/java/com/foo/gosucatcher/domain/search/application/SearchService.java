package com.foo.gosucatcher.domain.search.application;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foo.gosucatcher.domain.item.application.dto.response.sub.SubItemsResponse;
import com.foo.gosucatcher.domain.item.domain.SubItem;
import com.foo.gosucatcher.domain.item.domain.SubItemRepository;
import com.foo.gosucatcher.domain.search.application.dto.response.SearchListResponse;
import com.foo.gosucatcher.domain.search.application.dto.response.SearchRankingListResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SearchService {

	private static final int MAXIMUM_SAVED_VALUE = 5;
	private static final String SEARCH_KEY = "search::";
	private static final String SEARCH_COUNT_KEY = "search_count::";
	private static final String POPULAR_KEYWORDS_KEY = "popular_keywords";

	@Qualifier("searchRedisTemplate")
	private final RedisTemplate<String, String> redisTemplate;
	private final SubItemRepository subItemRepository;

	public SubItemsResponse searchKeyword(Long memberId, String keyword) {
		if (isInvalidKeyword(keyword)) return null;

		String key = SEARCH_KEY + memberId;

		List<SubItem> subItems = subItemRepository.findByNameContains(keyword);

		if (!subItems.isEmpty()) {
			addKeywordInRedis(keyword, key);
			incrementSearchCount(keyword);
			updatePopularKeywordsList(keyword);
		}

		return SubItemsResponse.from(subItems);
	}

	@Transactional(readOnly = true)
	public SearchListResponse getResentSearchList(Long memberId) {

		String key = SEARCH_KEY + memberId;

		ListOperations<String, String> listOperations = redisTemplate.opsForList();

		List<String> range = listOperations.range(key, 0, listOperations.size(key));
		Collections.reverse(range);

		return SearchListResponse.from(range);
	}

	@Transactional(readOnly = true)
	public SearchRankingListResponse getPopularKeywords() {
		Map<String, Long> keywordCounts = getKeywordCounts();

		List<String> popularKeywords = getTopFivePopularKeywords(keywordCounts);

		return SearchRankingListResponse.from(popularKeywords);
	}

	private boolean isInvalidKeyword(String keyword) {
		return keyword == null || keyword.isBlank() || keyword.isEmpty();
	}

	private void addKeywordInRedis(String keyword, String key) {
		ListOperations<String, String> listOperations = redisTemplate.opsForList();
		boolean isKeywordInRedis = false;

		for (String pastKeyword : listOperations.range(key, 0, listOperations.size(key))) {
			if (pastKeyword.equals(keyword)) {
				isKeywordInRedis = true;
				break;
			}
		}
		if (!isKeywordInRedis) {
			if (listOperations.size(key) == MAXIMUM_SAVED_VALUE) {
				listOperations.leftPop(key);
			}
			listOperations.rightPush(key, keyword);
		}
	}

	private void incrementSearchCount(String keyword) {
		String countKey = SEARCH_COUNT_KEY + keyword;

		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

		valueOperations.increment(countKey);
	}

	private Map<String, Long> getKeywordCounts() {
		Map<String, Long> keywordCounts = new HashMap<>();
		Set<String> keys = redisTemplate.keys(SEARCH_COUNT_KEY + "*");

		for (String key : keys) {
			String keyword = key.replace(SEARCH_COUNT_KEY, "");
			long count = Long.parseLong(redisTemplate.opsForValue().get(key));

			keywordCounts.put(keyword, count);
		}

		return keywordCounts;
	}

	private List<String> getTopFivePopularKeywords(Map<String, Long> keywordCounts) {
		List<String> popularKeywords = keywordCounts.entrySet()
			.stream()
			.sorted(Map.Entry.<String, Long>comparingByValue().reversed())
			.limit(MAXIMUM_SAVED_VALUE)
			.map(Map.Entry::getKey)
			.toList();

		return popularKeywords;
	}

	private void updatePopularKeywordsList(String keyword) {
		Long currentCount = redisTemplate.opsForList().size(POPULAR_KEYWORDS_KEY);

		if (currentCount >= MAXIMUM_SAVED_VALUE) {
			redisTemplate.opsForList().trim(POPULAR_KEYWORDS_KEY, 0, MAXIMUM_SAVED_VALUE - 1);
		}

		ListOperations<String, String> listOperations = redisTemplate.opsForList();
		listOperations.rightPush(POPULAR_KEYWORDS_KEY, keyword);
	}
}
