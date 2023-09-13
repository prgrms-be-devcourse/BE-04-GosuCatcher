package com.foo.gosucatcher.config;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EnableRedisRepositories(redisTemplateRef = "smsRedisTemplate")
@Configuration
public class SmsAuthRedisConfig {

	private final ObjectMapper objectMapper;
	private final RedisProperties redisProperties;

	@Bean
	public RedisConnectionFactory smsRedisConnectionFactory() {
		LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisProperties.getHost(),
			redisProperties.getPort());
		lettuceConnectionFactory.setDatabase(1);

		return lettuceConnectionFactory;
	}

	@Bean
	public RedisTemplate<String, Object> smsRedisTemplate() {
		final RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(smsRedisConnectionFactory());
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));

		return template;
	}
}
