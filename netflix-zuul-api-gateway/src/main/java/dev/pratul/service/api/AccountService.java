package dev.pratul.service.api;

import java.util.Set;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import dev.pratul.dto.AccountDto;

@FeignClient("account-service")
@RequestMapping(path = "/api")
public interface AccountService {

	/*
	 * Fetch the list of all active accounts for the user
	 */
	@GetMapping("/user/active/{userId}")
	Set<AccountDto> getActiveAccountByUserId(@PathVariable(value = "userId") long userId);

	/*
	 * Fetch the list of all the accounts for a user, irrespective of account status
	 */
	@GetMapping("/user/all/{userId}")
	Set<AccountDto> getAllAccountsByUser(@PathVariable(value = "userId") String userId);

	/*
	 * de-active the account
	 * 
	 * @input: accountId
	 */
	@PutMapping("/user/{accountId}")
	ResponseEntity<AccountDto> deactivateAccount(@PathVariable(value = "accountId") String accountId);
}
