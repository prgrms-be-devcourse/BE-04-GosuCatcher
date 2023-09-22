package com.foo.gosucatcher.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
	info = @Info(title = "User-Service API 명세서",
		description = "사용자 어플 서비스 API 명세서",
		version = "v1"))
@Configuration
public class SwaggerConfig {
	
}
