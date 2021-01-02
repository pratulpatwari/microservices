package dev.pratul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@EnableFeignClients
@EnableEurekaClient
@EnableZuulProxy
@SpringBootApplication
@ComponentScan("dev.pratul")
public class NetflixZuulApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(NetflixZuulApiGatewayApplication.class, args);
	}

	@Bean
	public HttpTraceRepository httpTraceRepository() {
		return new InMemoryHttpTraceRepository();
	}

	/*
	 * All API calls through FeignClient are intercepted using this bean to add the
	 * MDC context for logging in the header
	 */
	@Bean
	public CorrelationInterceptor correlation() {
		return new CorrelationInterceptor();
	}
}
