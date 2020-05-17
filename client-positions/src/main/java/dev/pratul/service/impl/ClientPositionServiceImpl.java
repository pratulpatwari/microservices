package dev.pratul.service.impl;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

import dev.pratul.ServiceConfig;
import dev.pratul.UserServiceException;
import dev.pratul.dao.ClientPositionRepository;
import dev.pratul.entity.Accounts;
import dev.pratul.entity.ClientPosition;
import dev.pratul.service.api.ClientPositionService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
class ClientPositionServiceImpl implements ClientPositionService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ClientPositionRepository clientPositionRepository;
	
	@Autowired
	private ServiceConfig serviceConfig;

	@HystrixCommand(fallbackMethod = "getFallbackClientPosition", commandProperties = {
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"),
			@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"),
			@HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
			@HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "1000"), })
	@Transactional
	public List<ClientPosition> getClientPositions(String clientId) {
		log.info("Entering getClientPositions() for clientId: {}", clientId);
		String accountServiceUrl = null;
		ResponseEntity<Accounts[]> accounts = null;
		List<Accounts> accountList = new ArrayList<Accounts>();
		List<ClientPosition> clientPositions = null;
		try {
			accountServiceUrl = serviceConfig.getAccount() + "user/" + clientId;
			accounts = restTemplate.getForEntity(accountServiceUrl, Accounts[].class);
		} catch (RestClientException ex) {
			log.error(
					"Error while fetching the accounts from account-service for user {}. The api called for account-service is: {}. Exception: {}",
					clientId, accountServiceUrl, ex);
		}
		if (accounts != null) {
			accountList = new ArrayList<>(List.of(accounts.getBody()));
			clientPositions = clientPositionRepository.findByAccountsInAndCreateDateBetween(accountList,
					ZonedDateTime.now().minusDays(7).with(LocalTime.MIN), ZonedDateTime.now());
		}
		log.info("Leaving getClientPositions() for clientId: {}, Total active accounts for clients: {}", clientId,
				accountList.size());
		return clientPositions;
	}

	@HystrixCommand(fallbackMethod = "getFallbackClientPosition", commandProperties = {
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"),
			@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"),
			@HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
			@HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000"), })
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

	@SuppressWarnings("unused")
	private List<ClientPosition> getFallbackClientPosition(String clientId) {
		log.error("Error while fetching the accounts from account-service for user {}. Entering getFallbackClientPosition()", clientId);
		// make a call to server cache to return the last fetched client-positions
		return null;
	}

}
