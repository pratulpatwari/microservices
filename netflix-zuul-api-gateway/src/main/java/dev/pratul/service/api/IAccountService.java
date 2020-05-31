package dev.pratul.service.api;

import java.util.Set;

import dev.pratul.model.Account;

public interface IAccountService{
	
	Set<Account> getAccountByUserId(String userId);
}
