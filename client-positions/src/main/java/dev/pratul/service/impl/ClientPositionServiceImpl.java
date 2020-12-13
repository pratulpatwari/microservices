package dev.pratul.service.impl;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import dev.pratul.ServiceConfig;
import dev.pratul.dao.ClientPositionRepository;
import dev.pratul.entity.Accounts;
import dev.pratul.entity.ClientPosition;
import dev.pratul.service.api.ClientPositionService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
class ClientPositionServiceImpl implements ClientPositionService {

	private final RestTemplate restTemplate;
	private final ClientPositionRepository clientPositionRepository;
	private final ServiceConfig serviceConfig;

	public ClientPositionServiceImpl(ClientPositionRepository clientPositionRepository, ServiceConfig serviceConfig,
			RestTemplate restTemplate) {
		this.clientPositionRepository = clientPositionRepository;
		this.serviceConfig = serviceConfig;
		this.restTemplate = restTemplate;
	}

	@HystrixCommand(fallbackMethod = "getFallbackClientPosition", commandKey = "clientPosition")
	@Transactional
	public List<ClientPosition> getClientPositions(String clientId) {
		log.info("Entering getClientPositions() for clientId: {}", clientId);
		List<ClientPosition> clientPositions = new ArrayList<>();
		ResponseEntity<Accounts[]> accounts = getAccountsByIds(clientId);
		if (accounts != null && accounts.getBody() != null) {
			List<Accounts> accountList = new ArrayList<>(List.of(accounts.getBody()));
			clientPositions.addAll(clientPositionRepository.findByAccountsInAndCreateDateBetween(accountList,
					ZonedDateTime.now().minusDays(7).with(LocalTime.MIN), ZonedDateTime.now()));
		} else {
			throw new RestClientException(
					"Call to account-service failed. Calling cache to return last fetched values");
		}
		log.info("Leaving getClientPositions() for clientId: {}, Total positions for clients: {}", clientId,
				clientPositions.size());
		return clientPositions;
	}

	private ResponseEntity<Accounts[]> getAccountsByIds(String clientId) {
		try {
			String accountServiceUrl = serviceConfig.getAccount() + "user/" + clientId;
			return restTemplate.getForEntity(accountServiceUrl, Accounts[].class);
		} catch (RestClientException ex) {
			log.error(
					"Error while fetching the accounts from account-service for user {}. The api called for account-service is: {}. Exception: {}",
					clientId, ex);
		}
		return null;
	}

	@HystrixCommand(fallbackMethod = "getFallbackClientPositionByAccount", commandKey = "clientPosition")
	@Transactional
	public List<ClientPosition> getClientPositionsByAccount(String clientId, String accountId) {
		log.info("Entering getClientPositionsByAccount() for client {} and accountId {}", clientId, accountId);
		Accounts accounts = new Accounts();
		accounts.setId(Long.valueOf(accountId));
		List<ClientPosition> clientPositions = clientPositionRepository.findByAccountsAndCreateDateBetween(accounts,
				ZonedDateTime.now().minusDays(7).with(LocalTime.MIN), ZonedDateTime.now());
		log.info("Leaving getClientPositionsByAccount() for client {} and accountId {}. The total # of positions are: ",
				clientId, accountId, clientPositions.size());
		return clientPositions;
	}

	/*
	 * Fallback method called by getClientPositions() using Hystrix fallback
	 * mechanism
	 * 
	 * input: clientId output: List of client positions for all the accounts for
	 * this user
	 */
	public List<ClientPosition> getFallbackClientPosition(String clientId) {
		log.warn(
				"Error while fetching the accounts from account-service for user {}. Entering getFallbackClientPosition()",
				clientId);

		// make a call to server cache to return the last fetched client-positions
		return new ArrayList<>();
	}

	/*
	 * Fallback method called by getClientPositions() using Hystrix fallback
	 * mechanism
	 * 
	 * input: clientId, accountId output: List of client positions for the mentioned
	 * accounts for this user
	 */
	public List<ClientPosition> getFallbackClientPositionByAccount(String clientId, String accountId) {
		log.warn(
				"Error while fetching the accounts from account-service for user {} and account {}. Entering getFallbackClientPosition()",
				clientId, accountId);

		// make a call to server cache to return the last fetched client-positions
		return new ArrayList<>();
	}

}
