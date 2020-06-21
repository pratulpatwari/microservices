package dev.pratul.service.impl;

import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import dev.pratul.UserServiceException;
import dev.pratul.dao.AccountRepository;
import dev.pratul.entity.Accounts;
import dev.pratul.entity.Users;
import dev.pratul.service.api.AccountService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	private AccountRepository accountRepository;

	@Transactional
	public Accounts getAccountById(String id) {
		log.info("Entering getAccountById() {}", id);
		log.info("Leaving getAccountById() {}", id);
		return accountRepository.findById(Long.valueOf(id)).orElseThrow();
	}

	@Transactional
	public Set<Accounts> getActiveAccountsByUser(String userId) {
		log.info("Entering getActiveAccountsByUser() for userId: {}", userId);
		Users user = new Users();
		user.setId(Long.valueOf(userId));
		log.info("Leaving getActiveAccountsByUser() for userId: {}", userId);
		return accountRepository.findByUsersAndStatusTrue(user);
	}

	@Transactional
	public Set<Accounts> getAllAccountsByUser(String userId) {
		log.info("Entering getAllAccountsByUser() for userId: {}", userId);
		Users user = new Users();
		user.setId(Long.valueOf(userId));
		log.info("Leaving getAllAccountsByUser() for userId: {}", userId);
		return accountRepository.findByUsers(user);
	}

	@Transactional
	public ResponseEntity<Accounts> deactivateAccount(String accountId) throws UserServiceException {
		log.info("Entering deactivateAccount() for accountId: {}", accountId);
		Accounts account = accountRepository.findById(Long.valueOf(accountId))
				.orElseThrow(() -> new UserServiceException("Account not found :: " + accountId));
		account.setStatus(false);
		final Accounts deactiveAccount = accountRepository.save(account);
		log.info("Leaving deactivateAccount() for accountId: {}", accountId);
		return ResponseEntity.ok(deactiveAccount);
	}

}
