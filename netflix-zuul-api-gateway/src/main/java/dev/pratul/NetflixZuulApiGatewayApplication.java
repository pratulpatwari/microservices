package dev.pratul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@EnableEurekaClient
@EnableZuulProxy
@EnableCircuitBreaker
@SpringBootApplication
public class NetflixZuulApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(NetflixZuulApiGatewayApplication.class, args);
	}

}
