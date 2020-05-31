package dev.pratul.service.impl;

import java.util.Set;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.pratul.model.Account;
import dev.pratul.service.api.AccountService;
import lombok.Getter;

@Component
public class ServiceCallables implements Callable<Set<Account>> {

	@Autowired
	private AccountService accountService;
	
	@Getter
	private int counter;
	
	@Override
	public Set<Account> call() throws Exception {
		counter++;
		System.out.println("Thread : " + Thread.currentThread() + "    Current value: " + counter);
		return accountService.getAccountByUserId("1");
	}
	
	

}
