package com.foo.gosucatcher.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "secret.coolsms")
public class SmsAuthProperties {

	private String fromNumber;
	private String apiKey;
	private String apiSecret;
	private String domain;
	private Long expirationTime;
}
