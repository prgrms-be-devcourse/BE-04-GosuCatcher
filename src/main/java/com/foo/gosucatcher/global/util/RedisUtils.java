package com.foo.gosucatcher.global.util;

import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisUtils {

	private final RedisTemplate<String, Object> redisTemplate;
	private final ModelMapper modelMapper;

	public void put(String key, Object value, Long expirationTime) {
		ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
		if (expirationTime != null) {
			valueOperations.set(key, value, expirationTime, TimeUnit.SECONDS);
		} else {
			valueOperations.set(key, value);
		}
	}

	public void delete(String key) {
		redisTemplate.delete(key);
	}

	public <T> T get(String key, Class<T> clazz) {
		ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

		Object value = valueOperations.get(key);
		if (value != null) {
			if (value instanceof LinkedHashMap) {
				return modelMapper.map(value, clazz);
			} else {
				return clazz.cast(value);
			}
		}

		return null;
	}

	public boolean isExists(String key) {
		return redisTemplate.hasKey(key);
	}

	public void setExpireTime(String key, long expirationTime) {
		redisTemplate.expire(key, expirationTime, TimeUnit.SECONDS);
	}

	public long getExpireTime(String key) {
		return redisTemplate.getExpire(key, TimeUnit.SECONDS);
	}
}
