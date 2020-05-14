package dev.pratul.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

import dev.pratul.dao.ClientPositionRepository;
import dev.pratul.entity.Accounts;
import dev.pratul.entity.ClientPosition;
import dev.pratul.entity.Users;
import dev.pratul.service.api.AccountService;
import dev.pratul.service.api.ClientPositionService;

@Service
class ClientPositionServiceImpl implements ClientPositionService {

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private ClientPositionRepository clientPositionRepository;

	@Autowired
	private AccountService accountService;
	
	@Value("${user.service.uri}")
	private String userServiceUri;

	@HystrixCommand(fallbackMethod = "getFallbackClientPosition", commandProperties = {
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"),
			@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"),
			@HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
			@HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000"), })
	@Transactional
	public List<ClientPosition> getClientPositions(String clientId) {
		Users user = null;
		try {
			user = restTemplate.getForObject(userServiceUri + clientId, Users.class);
		} catch(Exception ex) {
			System.out.println(ex);
		} finally {
			System.out.println(user);
		}
		Accounts accounts = accountService.getAccountById(clientId);
		return clientPositionRepository.findByAssetId(Long.valueOf(1));
	}
	
	@Transactional
	public List<ClientPosition> getClientPositionsByAccount(String clientId, String accountId) {
		
		return null;
	}
	
	@SuppressWarnings("unused")
	private List<ClientPosition> getFallbackClientPosition(String clientId){
		return null;
	}

	

}
