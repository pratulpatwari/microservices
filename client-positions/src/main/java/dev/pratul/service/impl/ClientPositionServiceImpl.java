package dev.pratul.service.impl;

import dev.pratul.ServiceConfig;
import dev.pratul.dto.AccountDto;
import dev.pratul.model.ClientPositionSummary;
import dev.pratul.service.api.ClientPositionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
class ClientPositionServiceImpl implements ClientPositionService {

	private final ServiceConfig serviceConfig;
	private final WebClient.Builder webClient;

	public ClientPositionServiceImpl(ServiceConfig serviceConfig, WebClient.Builder webClient) {
		this.serviceConfig = serviceConfig;
		this.webClient = webClient;
	}

	// @HystrixCommand(fallbackMethod = "getFallbackClientPosition", commandKey =
	// "clientPosition")
	@Transactional(readOnly = true)
	public Mono<ClientPositionSummary> getClientPositions(long userId, Map<String, String> params) {
		log.debug("Entering getClientPositions() for user {} with number of parameters: {}", userId,
				params.size());
		for (Map.Entry<String, String> entry : params.entrySet()) {
			// query using user and other params
		}
		log.debug("Leaving getClientPositions() for user {} with number of parameters: {}", userId,
				params.size());
		return null;
	}

	public List<ClientPositionSummary> getFallbackClientPosition(long userId, Map<String, String> params) {
		log.warn("Error while fetching the accounts from account-service for user {}. Entering getFallbackClientPosition()",
				userId);

		// make a call to server cache to return the last fetched client-positions
		return List.of();
	}

	private Mono<AccountDto[]> getAccountsByUserId(long clientId) {
		try {
			return webClient.build().get().uri(
					String.join("", serviceConfig.getAccount(), "user/", String.valueOf(clientId)))
					.retrieve().bodyToMono(AccountDto[].class);
		} catch (Exception ex) {
			log.error("Error while fetching the accounts from account-service for user {}. The api called for account-service is: {}. Exception: {}",
					clientId, ex);
		}
		return null;
	}
}
