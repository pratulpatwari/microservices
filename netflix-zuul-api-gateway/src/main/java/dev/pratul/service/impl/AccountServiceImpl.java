package dev.pratul.service.impl;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import dev.pratul.model.Account;
import dev.pratul.service.api.IAccountService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AccountServiceImpl implements IAccountService {

//	@Autowired
//	private AccountService accountService;

	@Autowired
	ServiceCallables serviceCall;

	@Override
	@HystrixCommand(fallbackMethod = "getFallbackAccountByUserId", commandKey = "zuulGateway")
	public Set<Account> getAccountByUserId(String userId) {
		log.info("Entering getAccountByUserId() for user {}", userId);
		Set<Account> accounts = new HashSet<>();
		ExecutorService executor = null;
		try {
			executor = Executors.newFixedThreadPool(10);
			List<Future<Set<Account>>> list = new LinkedList<Future<Set<Account>>>();
			List<ServiceCallables> task = new LinkedList<>();
			for (int i = 0; i < 5; i++) {
				task.add(serviceCall);
			}
			list = executor.invokeAll(task);
			for (int i = 0; i < list.size(); i++) {
				
				log.info("Counter value for thread: {}", serviceCall.getCounter());
				Future<Set<Account>> future = list.get(i);
				try {
					Set<Account> result = future.get();
					accounts.addAll(result);
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			executor.shutdown();
		}
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
