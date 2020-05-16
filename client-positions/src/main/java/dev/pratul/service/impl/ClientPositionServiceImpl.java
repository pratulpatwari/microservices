package dev.pratul.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

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

	@Value("${user.service.uri}")
	private String userServiceUri;

	@Value("${account.service.uri}")
	private String accountServiceUri;

	@HystrixCommand(fallbackMethod = "getFallbackClientPosition", commandProperties = {
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"),
			@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"),
			@HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
			@HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000"), })
	@Transactional
	public List<ClientPosition> getClientPositions(String clientId) {
		log.info("Entering getClientPositions {}", clientId);
		ResponseEntity<Accounts[]> accounts = null;
		try {
			String accountServiceUrl = accountServiceUri + "user/" + clientId;
			accounts = restTemplate.getForEntity(accountServiceUrl, Accounts[].class);
		} catch (RestClientException ex) {
			log.error("Error while fetching the user from account-service: ", ex);
		}
		List<Accounts> accountList = new ArrayList<>(List.of(accounts.getBody()));
		log.info("Leaving getClientPositions {}, Total accounts for clients: {}", clientId, accountList.size());
		return clientPositionRepository.findByAccountsIn(accountList);
	}

	@Transactional
	public List<ClientPosition> getClientPositionsByAccount(String clientId, String accountId) {

		return null;
	}

	@SuppressWarnings("unused")
	private List<ClientPosition> getFallbackClientPosition(String clientId) {

		// make a call to server cache to return the last fetched client-positions
		return null;
	}

}
