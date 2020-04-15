package dev.pratul.service.api;

import dev.pratul.entity.Accounts;

public interface AccountService {
	
	public Accounts getAccountById(String accountId);
}
