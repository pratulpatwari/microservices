package dev.pratul.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import dev.pratul.ServiceConfig;
import dev.pratul.model.Account;
import dev.pratul.model.ClientPositionSummary;
import dev.pratul.model.Position;
import dev.pratul.service.api.ClientPositionService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
class ClientPositionServiceImpl implements ClientPositionService {

	private final RestTemplate restTemplate;
	private final ServiceConfig serviceConfig;
	private final JdbcTemplate jdbcTemplate;

	public ClientPositionServiceImpl(ServiceConfig serviceConfig, RestTemplate restTemplate,
			JdbcTemplate jdbcTemplate) {
		this.serviceConfig = serviceConfig;
		this.restTemplate = restTemplate;
		this.jdbcTemplate = jdbcTemplate;
	}

	@HystrixCommand(fallbackMethod = "getFallbackClientPosition", commandKey = "clientPosition")
	@Transactional
	public List<ClientPositionSummary> getClientPositions(long userId, Map<String, String> params) {
		log.debug("Entering getClientPositions() for user {} with number of parameters: {}", userId, params.size());
		for (Map.Entry<String, String> entry : params.entrySet()) {
			// query using user and other params
			
		}
		log.debug("Leaving getClientPositions() for user {} with number of parameters: {}", userId, params.size());
		return null;
	}

	public List<ClientPositionSummary> getFallbackClientPosition(long userId, Map<String, String> params) {
		log.warn(
				"Error while fetching the accounts from account-service for user {}. Entering getFallbackClientPosition()",
				userId);

		// make a call to server cache to return the last fetched client-positions
		return List.of();
	}

	private LocalDate getLocalDateByString(String date) {
		if (!date.isBlank()) {
			try {
				return LocalDate.parse(date.strip());
			} catch (DateTimeParseException ex) {
				log.error("Exception while parsing from date: {}", ex.getMessage());
			}
		}
		return null;
	}

	@HystrixCommand(fallbackMethod = "getFallbackClientPosition", commandKey = "clientPosition")
	@Transactional
	public List<ClientPositionSummary> getClientPositions(String clientId, Optional<String> from, Optional<String> to) {
		log.info("Entering getClientPositions() for clientId: {}", clientId);
		List<ClientPositionSummary> clientPositions = new ArrayList<>();
		ResponseEntity<Account[]> accounts = getAccountsByUserId(clientId);
		if (accounts != null && accounts.getBody() != null) {
			List<Account> accountList = new ArrayList<>(List.of(accounts.getBody()));
			LocalDate fromDate = LocalDate.now().minusMonths(3);
			if (!from.isEmpty()) {
				fromDate = getLocalDateByString(from.get());
			}
			LocalDate toDate = LocalDate.now();
			if (!to.isEmpty()) {
				toDate = getLocalDateByString(to.get());
			}
		} else {
			throw new RestClientException(
					"Call to account-service failed. Calling cache to return last fetched values");
		}
		log.info("Leaving getClientPositions() for clientId: {}, Total positions for clients: {}", clientId,
				clientPositions.size());
		return clientPositions;
	}

	private ResponseEntity<Account[]> getAccountsByUserId(String clientId) {
		try {
			String accountServiceUrl = serviceConfig.getAccount() + "account/user/active/" + clientId;
			return restTemplate.getForEntity(accountServiceUrl, Account[].class);
		} catch (RestClientException ex) {
			log.error(
					"Error while fetching the accounts from account-service for user {}. The api called for account-service is: {}. Exception: {}",
					clientId, ex);
		}
		return null;
	}

	/*
	 * Fallback method called by getClientPositions() using Hystrix fallback
	 * mechanism
	 * 
	 * input: clientId output: List of client positions for all the accounts for
	 * this user
	 */
	public List<Position> getFallbackClientPosition(String clientId, Optional<String> from, Optional<String> to) {
		log.warn(
				"Error while fetching the accounts from account-service for user {}. Entering getFallbackClientPosition()",
				clientId);

		// make a call to server cache to return the last fetched client-positions
		return List.of();
	}
}
