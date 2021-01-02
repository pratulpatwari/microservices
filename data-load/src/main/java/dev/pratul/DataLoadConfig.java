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
@ConfigurationProperties(value = "data.load")
public class DataLoadConfig {

	private String uri;
	private String token;
	private String stocks;
	private String quote;
}
