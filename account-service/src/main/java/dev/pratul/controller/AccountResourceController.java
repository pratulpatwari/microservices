package dev.pratul.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.pratul.dto.AccountDto;
import dev.pratul.service.api.AccountService;

@RestController
@RequestMapping("/api")
public class AccountResourceController {

	private final AccountService accountService;

	public AccountResourceController(AccountService accountService) {
		this.accountService = accountService;
	}

	@GetMapping("/{id}")
	public ResponseEntity<AccountDto> getAccountById(@PathVariable(value = "id") long id) {
		return new ResponseEntity<>(accountService.getAccountById(id), HttpStatus.OK);
	}

	@GetMapping("/details/{id}")
	public ResponseEntity<AccountDto> getAccountDetailsById(@PathVariable(value = "id") long id) {
		return new ResponseEntity<>(accountService.getAccountDetailsById(id), HttpStatus.OK);
	}

	@PutMapping("/deactive")
	public ResponseEntity<List<AccountDto>> deactivateAccount(@RequestParam("accounts") String accounts) {
		return new ResponseEntity<>(accountService.deactivateAccount(accounts), HttpStatus.OK);
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<List<AccountDto>> getAllAccountsByUser(@PathVariable(value = "userId") long userId) {
		return new ResponseEntity<>(accountService.getAllAccountsByUser(userId), HttpStatus.OK);
	}

	@PutMapping("/user")
	public ResponseEntity<List<AccountDto>> updateUserAccount(@RequestBody @Valid List<AccountDto> accountDto) {
		return new ResponseEntity<>(accountService.updateUserAccount(accountDto), HttpStatus.OK);
	}

	@PostMapping("/add")
	public ResponseEntity<AccountDto> addAccount(@RequestBody @Valid AccountDto accountDto) {
		return new ResponseEntity<>(accountService.addAccount(accountDto), HttpStatus.CREATED);
	}
}
