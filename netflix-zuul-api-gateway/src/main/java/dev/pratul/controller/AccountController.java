package dev.pratul.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import dev.pratul.model.Account;
import dev.pratul.service.api.AccountService;

@RestController
public class AccountController {

	@Autowired
	private AccountService accountService;
	
	@GetMapping("api/user/{id}")
	public Set<Account> getAccount(@PathVariable("id") String id) {
		Set<Account> result = accountService.getAccount(id);
		return result;
	}
}
