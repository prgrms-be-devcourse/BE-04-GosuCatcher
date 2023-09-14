package com.foo.gosucatcher.domain.search.application;

import java.util.List;

import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foo.gosucatcher.domain.item.application.dto.response.sub.SubItemsResponse;
import com.foo.gosucatcher.domain.item.domain.SubItem;
import com.foo.gosucatcher.domain.item.domain.SubItemRepository;
import com.foo.gosucatcher.domain.search.application.dto.response.SearchListResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SearchService {

	private final RedisTemplate<String, String> redisTemplate;
	private final SubItemRepository subItemRepository;

	public SubItemsResponse searchKeyword(Long memberId, String keyword) {
		if (isInvalidKeyword(keyword)) return null;

		String key = "search::" + memberId;

		List<SubItem> subItems = subItemRepository.findByNameContains(keyword);

		if (!subItems.isEmpty()) {
			ListOperations<String, String> listOperations = redisTemplate.opsForList();
			boolean isKeywordInRedis = false;

			for (String pastKeyword : listOperations.range(key, 0, listOperations.size(key))) {
				if (pastKeyword.equals(keyword)) {
					isKeywordInRedis = true;
					break;
				}
			}
			if (!isKeywordInRedis) {
				if (listOperations.size(key) == 5) {
					listOperations.leftPop(key);
				}
				listOperations.rightPush(key, keyword);
			}
		}

		return SubItemsResponse.from(subItems);
	}

	@Transactional(readOnly = true)
	public SearchListResponse getSearchList(Long memberId) {

		String key = "search::" + memberId;

		ListOperations<String, String> listOperations = redisTemplate.opsForList();

		List<String> range = listOperations.range(key, 0, listOperations.size(key));

		return SearchListResponse.from(range);
	}

	private boolean isInvalidKeyword(String keyword) {
		return keyword == null || keyword.isBlank() || keyword.isEmpty();
	}
}
