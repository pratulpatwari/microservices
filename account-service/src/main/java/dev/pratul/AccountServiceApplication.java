package dev.pratul;

import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

@SpringBootApplication
@EnableEurekaClient
@EnableJpaRepositories(basePackages = { "dev.pratul" })
@EntityScan(basePackages = { "dev.pratul" })
@EnableTransactionManagement
@EnableCircuitBreaker
// @EnableWebFlux
// @ComponentScan("dev.pratul")
public class AccountServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountServiceApplication.class, args);
	}

	private static final int TIMEOUT = 1000;

	@Bean(name = "dataSource")
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource jpaDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean(name = "transactionManager")
	public PlatformTransactionManager transactionManager(
			@Qualifier("entityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory) {
		if (entityManagerFactory != null) {
			EntityManagerFactory entityManager = entityManagerFactory.getObject();
			if (entityManager != null) {
				return new JpaTransactionManager(entityManager);
			}
		}
		return null;
	}

	@Bean
	@LoadBalanced //enables integration with service discovery and load balancing using the Netflix OSS Ribbon clientO
	public WebClient.Builder webClientWithTimeout() {
		TcpClient tcpClient = TcpClient.create().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT)
				.doOnConnected(connection -> {
					connection.addHandlerLast(
							new ReadTimeoutHandler(TIMEOUT, TimeUnit.MILLISECONDS));
					connection.addHandlerLast(
							new WriteTimeoutHandler(TIMEOUT, TimeUnit.MILLISECONDS));
				});
		return WebClient.builder().clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)));
	}

	@Bean
	public HttpTraceRepository httpTraceRepository() {
		return new InMemoryHttpTraceRepository();
	}
}
