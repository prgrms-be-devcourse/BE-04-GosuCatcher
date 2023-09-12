package com.foo.gosucatcher.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class RedisCommonConfigure {

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		org.modelmapper.config.Configuration configuration = modelMapper.getConfiguration();
		configuration.setMatchingStrategy(MatchingStrategies.STRICT);

		return modelMapper;
	}
}
