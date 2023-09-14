package com.foo.gosucatcher.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories(redisTemplateRef = "searchRedisTemplate")
public class RedisConfig {

	@Value("${spring.redis.host}")
	private String host;

	@Value("${spring.redis.port}")
	private int port;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory(host, port);
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
