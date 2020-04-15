package dev.pratul.service.impl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.pratul.dao.AccountRepository;
import dev.pratul.entity.Accounts;
import dev.pratul.service.api.AccountService;

@Service
public class AccountServiceImpl implements AccountService{

	@Autowired
	private AccountRepository accountRepository;
	
	@Transactional
	public Accounts getAccountById(String accountId) {
		return accountRepository.findByAccountId(accountId);
	}

}
