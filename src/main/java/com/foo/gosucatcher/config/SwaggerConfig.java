package com.foo.gosucatcher.config;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;

@OpenAPIDefinition(
	info = @Info(title = "Gosu Catcher API 명세서",
		description = "에프팀의 고수 캐처 API 명세서",
		version = "v1"))
@RequiredArgsConstructor
@Configuration
public class SwaggerConfig {

	@Bean
	public GroupedOpenApi chatOpenApi() {
		String[] paths = {"/api/v1/**"};

		return GroupedOpenApi.builder()
			.group("Gosu Catcher API 명세서")
			.pathsToMatch(paths)
			.build();
	}
}