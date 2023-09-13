package com.foo.gosucatcher.global.util;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RandomElementSelector {

	public static <T> List<T> selectRandomElements(List<T> list, int count) {
		if (list == null || list.isEmpty() || count <= 0) {
			return List.of();
		}

		if (count >= list.size()) {
			return list;
		}

		Random random = new Random();
		return random.ints(0, list.size())
			.distinct()
			.limit(count)
			.mapToObj(list::get)
			.collect(Collectors.toList());
	}
}
