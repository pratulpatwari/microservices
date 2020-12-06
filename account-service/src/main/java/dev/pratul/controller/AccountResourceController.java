package dev.pratul.controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
		AccountDto accountDto = accountService.getAccountById(id);
		return new ResponseEntity<>(accountDto, accountDto != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}

	@GetMapping("/details/{id}")
	public ResponseEntity<AccountDto> getAccountDetailsById(@PathVariable(value = "id") String id) {
		AccountDto accountDto = accountService.getAccountDetailsById(id);
		return new ResponseEntity<>(accountDto, accountDto != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}

	@PutMapping("/{accountId}")
	public ResponseEntity<AccountDto> deactivateAccount(@PathVariable(value = "accountId") String accountId) {
		List<AccountDto> dto = accountService.updateAccountStatus(Stream.of(accountId).collect(Collectors.toList()));
		return new ResponseEntity<>(dto.get(0),
				dto != null && !dto.isEmpty() ? HttpStatus.OK : HttpStatus.PRECONDITION_FAILED);
	}

	@GetMapping("/user/active/{id}")
	public ResponseEntity<List<AccountDto>> getActiveAccountsByUser(@PathVariable(value = "id") String userId) {
		List<AccountDto> accounts = accountService.getActiveAccountsByUser(userId);
		return new ResponseEntity<>(accounts, HttpStatus.OK);
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<List<AccountDto>> getAllAccountsByUser(@PathVariable(value = "userId") String userId) {
		List<AccountDto> accounts = accountService.getAllAccountsByUser(userId);
		return new ResponseEntity<>(accounts, HttpStatus.OK);
	}

	@PutMapping("/user")
	public ResponseEntity<List<AccountDto>> deactivateAccount(@RequestBody @Valid List<AccountDto> accountDto) {
		List<AccountDto> deactiveAccount = accountService.updateUserAccount(accountDto);
		return new ResponseEntity<>(deactiveAccount, deactiveAccount != null ? HttpStatus.OK : HttpStatus.NOT_MODIFIED);
	}

	@PostMapping("/add")
	public ResponseEntity<AccountDto> addAccount(@RequestBody @Valid AccountDto accountDto) {
		AccountDto account = accountService.addAccount(accountDto);
		return new ResponseEntity<>(account, account != null ? HttpStatus.CREATED : HttpStatus.NO_CONTENT);
	}
}
