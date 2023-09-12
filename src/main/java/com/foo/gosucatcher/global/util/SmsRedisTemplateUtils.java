package com.foo.gosucatcher.global.util;

import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Component
public class SmsRedisTemplateUtils {

	private final RedisTemplate<String, Object> smsRedisTemplate;
	private final ModelMapper modelMapper;

	public SmsRedisTemplateUtils(@Qualifier("smsRedisTemplate") RedisTemplate<String, Object> smsRedisTemplate,
		ModelMapper modelMapper) {
		this.smsRedisTemplate = smsRedisTemplate;
		this.modelMapper = modelMapper;
	}

	public void put(String key, Object value, Long expirationTime) {
		ValueOperations<String, Object> valueOperations = smsRedisTemplate.opsForValue();
		if (expirationTime != null) {
			valueOperations.set(key, value, expirationTime, TimeUnit.SECONDS);
		} else {
			valueOperations.set(key, value);
		}
	}

	public void delete(String key) {
		smsRedisTemplate.delete(key);
	}

	public <T> T get(String key, Class<T> clazz) {
		ValueOperations<String, Object> valueOperations = smsRedisTemplate.opsForValue();

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
		return smsRedisTemplate.hasKey(key);
	}

	public void setExpireTime(String key, long expirationTime) {
		smsRedisTemplate.expire(key, expirationTime, TimeUnit.SECONDS);
	}

	public long getExpireTime(String key) {
		return smsRedisTemplate.getExpire(key, TimeUnit.SECONDS);
	}
}
