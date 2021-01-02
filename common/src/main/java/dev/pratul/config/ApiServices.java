package dev.pratul.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@RefreshScope
@ConfigurationProperties(value = "service.uri")
public class ApiServices {
	
	private String user;
	private String account;
	private String position;
}
