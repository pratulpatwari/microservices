package dev.pratul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableEurekaClient
@EnableJpaRepositories(basePackages = { "dev.pratul" })
public class ClientPositionsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClientPositionsApplication.class, args);
	}

}
