package dev.pratul.controller;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.pratul.dto.AccountDto;
import dev.pratul.dto.UserDto;
import dev.pratul.service.api.AccountService;
import dev.pratul.service.api.UserService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
public class UserAccountController {

	private final AccountService accountService;
	private final UserService userService;

	public UserAccountController(AccountService accountService, UserService userService) {
		this.accountService = accountService;
		this.userService = userService;
	}

	@GetMapping("/account/user/{userId}")
	public ResponseEntity<Set<AccountDto>> getAccount(@PathVariable("userId") long userId) {
		log.debug("Entering {}", userId);
		Set<AccountDto> accounts = accountService.getActiveAccountByUserId(userId);
		log.debug("Leaving: {}", userId);
		return new ResponseEntity<>(accounts, HttpStatus.OK);
	}

	@GetMapping("/user/all")
	public ResponseEntity<Set<UserDto>> getUsersByOrganization() {
		return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
	}
}
