package dev.pratul;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Configuration
@RefreshScope
@ConfigurationProperties(value = "service.uri")
public class ApiServices {
	
	private String account;
}
