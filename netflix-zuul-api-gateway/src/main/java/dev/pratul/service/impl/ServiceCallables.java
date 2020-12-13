package dev.pratul.service.impl;

import java.util.Set;
import java.util.concurrent.Callable;

import org.springframework.stereotype.Component;

import dev.pratul.model.Account;
import dev.pratul.service.api.AccountService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ServiceCallables implements Callable<Set<Account>> {

	private final AccountService accountService;
	
	public ServiceCallables(AccountService accountService) {
		this.accountService = accountService;
	}

	@Getter
	private int counter;

	@Override
	public Set<Account> call() throws Exception {
		counter++;
		log.debug("Thread : {}. Current value: {}",Thread.currentThread(),counter);
		return accountService.getAccountByUserId("1");
	}
}
