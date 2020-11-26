package dev.pratul.service.api;

import java.util.Set;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import dev.pratul.model.Account;

@FeignClient("account-service")
public interface AccountService {

	/*
	 * Fetch the list of all active accounts for the user
	 */
	@GetMapping("/api/account/user/{userId}")
	@CrossOrigin
	Set<Account> getAccountByUserId(@PathVariable(value = "userId") String userId);

	/*
	 * Fetch the list of all the accounts for a user, irrespective of account status
	 */
	@GetMapping("/api/account/user/all/{userId}")
	@CrossOrigin
	Set<Account> getAllAccountsByUser(@PathVariable(value = "userId") String userId);

	/*
	 * de-active the account
	 * 
	 * @input: accountId
	 */
	@PutMapping("/api/account/user/{accountId}")
	@CrossOrigin
	ResponseEntity<Account> deactivateAccount(@PathVariable(value = "accountId") String accountId);
}
