package com.foo.gosucatcher.config;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableRedisRepositories(redisTemplateRef = "searchRedisTemplate")
@RequiredArgsConstructor
public class RedisConfig {

	private final RedisProperties redisProperties;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisProperties.getHost(), redisProperties.getPort());
		lettuceConnectionFactory.setDatabase(2);

		return lettuceConnectionFactory;
	}

	@Bean
	public RedisTemplate<String, String> searchRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, String> searchRedisTemplate = new RedisTemplate<>();
		searchRedisTemplate.setConnectionFactory(redisConnectionFactory);
		searchRedisTemplate.setKeySerializer(new StringRedisSerializer());
		searchRedisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));

		return searchRedisTemplate;
	}
}
