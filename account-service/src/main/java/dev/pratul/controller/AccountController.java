package dev.pratul.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.pratul.entity.Accounts;
import dev.pratul.service.api.AccountService;

@RefreshScope
@RestController
@RequestMapping("/api/")
public class AccountController {

	@Autowired
	private AccountService accountService;
	
	@GetMapping("user/{id}")
	public Set<Accounts> getActiveAccountsByUser(@PathVariable String id){
		return accountService.getActiveAccountsByUser(id);
	}
	
	@GetMapping("user/all/{id}")
	public Set<Accounts> getAllAccountsByUser(@PathVariable String id){
		return accountService.getActiveAccountsByUser(id);
	}
}
