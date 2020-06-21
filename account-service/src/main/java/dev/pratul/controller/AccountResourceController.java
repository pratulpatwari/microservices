package dev.pratul.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.pratul.entity.Accounts;
import dev.pratul.service.api.AccountService;

@RefreshScope
@RestController
@RequestMapping("/api")
public class AccountResourceController {

	@Autowired
	private AccountService accountService;

	@GetMapping("/{id}")
	public Accounts getAccountById(@PathVariable(value = "id") String id) {
		return accountService.getAccountById(id);
	}

	@GetMapping("/user/{id}")
	public Set<Accounts> getActiveAccountsByUser(@PathVariable(value = "id") String userId) {
		return accountService.getActiveAccountsByUser(userId);
	}

	@GetMapping("/user/all/{id}")
	public Set<Accounts> getAllAccountsByUser(@PathVariable(value = "id") String userId) {
		return accountService.getActiveAccountsByUser(userId);
	}

	@PutMapping("/deactivate/{id}")
	public ResponseEntity<Accounts> deactivateAccount(@PathVariable(value = "id") String accountId) {
		return accountService.deactivateAccount(accountId);
	}
}
