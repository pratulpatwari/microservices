package dev.pratul.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.pratul.dto.AccountDto;
import dev.pratul.service.api.AccountService;

@RefreshScope
@RestController
@RequestMapping("/api/account")
public class AccountResourceController {

	@Autowired
	private AccountService accountService;

	@GetMapping("/{id}")
	public ResponseEntity<AccountDto> getAccountById(@PathVariable(value = "id") String id) {
		return new ResponseEntity<>(accountService.getAccountById(id), HttpStatus.OK);
	}

	@GetMapping("/user/{id}")
	public ResponseEntity<List<AccountDto>> getActiveAccountsByUser(@PathVariable(value = "id") String userId) {
		List<AccountDto> accounts = accountService.getActiveAccountsByUser(userId);
		return new ResponseEntity<>(accounts, HttpStatus.OK);
	}

	@GetMapping("/user/all/{id}")
	public ResponseEntity<List<AccountDto>> getAllAccountsByUser(@PathVariable(value = "id") String userId) {
		List<AccountDto> accounts = accountService.getAllAccountsByUser(userId);
		return new ResponseEntity<>(accounts, HttpStatus.OK);
	}

	@PutMapping("/deactivate/{userId}/{accountId}")
	public ResponseEntity<Boolean> deactivateAccount(@PathVariable(value = "userId") String userId,
			@PathVariable(value = "accountId") String accountId) {
		boolean deactiveAccount = accountService.deactivateUserAccount(userId, accountId);
		return new ResponseEntity<>(deactiveAccount, deactiveAccount ? HttpStatus.OK : HttpStatus.NOT_MODIFIED);
	}
	
	@PostMapping("/create")
	public ResponseEntity<AccountDto> addAccount(@RequestBody @Valid AccountDto accountDto) {
		AccountDto account = accountService.addAccount(accountDto);
		if(account != null) {
			return new ResponseEntity<>(account, HttpStatus.CREATED);
		}
		return new ResponseEntity<>(account, HttpStatus.NO_CONTENT); 
	}
}
