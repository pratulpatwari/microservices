package dev.pratul.service.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import dev.pratul.model.Account;
import dev.pratul.service.api.AccountService;
import dev.pratul.service.api.IAccountService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AccountServiceImpl implements IAccountService {

	@Autowired
	private AccountService accountService;

	@Override
	@HystrixCommand(fallbackMethod = "getFallbackAccountByUserId", commandKey = "zuulGateway")
	public Set<Account> getAccountByUserId(String userId) {
		log.info("Entering getAccountByUserId() for user {}", userId);
		Set<Account> accounts = accountService.getAccountByUserId(userId);
		Set<Account> allAccounts = accountService.getAllAccountsByUser(userId);
		
		log.info("Leaving getAccountByUserId() for user {}", userId);
		return accounts;
	}

	@SuppressWarnings("unused")
	private Set<Account> getFallbackAccountByUserId(String userId) {
		log.warn(
				"Error while fetching the accounts from account-service for user {}. Entering getFallbackClientPosition()",
				userId);

		// make a call to server cache to return the last fetched account-service
		return null;
	}

}
