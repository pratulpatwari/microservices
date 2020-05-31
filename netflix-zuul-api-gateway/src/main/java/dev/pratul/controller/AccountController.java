package dev.pratul.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.pratul.model.Account;
import dev.pratul.service.api.IAccountService;

@RestController
@RequestMapping("/api/account/")
//@PreAuthorize("hasRole('USER')")
public class AccountController {

	@Autowired
	private IAccountService accountService;

	@GetMapping("user/{userId}")
	public Set<Account> getAccount(@PathVariable("userId") String userId) {
		return accountService.getAccountByUserId(userId);
	}
}
