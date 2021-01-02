package dev.pratul.controller;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.pratul.model.Account;
import dev.pratul.model.User;
import dev.pratul.service.api.AccountService;
import dev.pratul.service.api.UserService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
public class UserAccountController {

	private final AccountService accountService;
	private final UserService userService;

	private UserAccountController(AccountService accountService, UserService userService) {
		this.accountService = accountService;
		this.userService = userService;
	}

	@GetMapping("/account/user/{userId}")
	public ResponseEntity<Set<Account>> getAccount(@PathVariable("userId") long userId) {
		log.debug("Entering getAccount with userId: {}", userId);
		return new ResponseEntity<>(accountService.getActiveAccountByUserId(userId), HttpStatus.OK);
	}

	@GetMapping("/user/all")
	public ResponseEntity<Set<User>> getUsersByOrganization() {
		return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
	}
}
