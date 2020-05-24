package dev.pratul.controller;

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
	
	@GetMapping("/user/{id}")
	public Account getAccount(@PathVariable("id") String id) {
		Account result = accountService.getAccount(id);
		return result;
	}
}
