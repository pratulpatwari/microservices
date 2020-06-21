package dev.pratul.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.pratul.UserServiceException;
import dev.pratul.dao.AccountRepository;
import dev.pratul.dto.AccountDto;
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
	public AccountDto getAccountById(String id) {
		log.info("Entering getAccountById() {}", id);
		Accounts account = accountRepository.findById(Long.valueOf(id))
				.orElseThrow(() -> new NullPointerException("Account not found"));
		log.info("Leaving getAccountById() {}", id);
		return new AccountDto(account.getId(), account.getAccountId(), account.getAccountName(), account.isStatus());
	}

	@Transactional
	public List<AccountDto> getActiveAccountsByUser(String userId) {
		log.info("Entering getActiveAccountsByUser() for userId: {}", userId);
		Users user = new Users();
		user.setId(Long.valueOf(userId));
		Set<Accounts> accounts = accountRepository.findByUsersAndStatusTrue(user);
		if (accounts != null && accounts.size() > 0) {
			List<AccountDto> accountsDto = new LinkedList<>();
			for (Accounts account : accounts) {
				accountsDto.add(new AccountDto(account.getId(), account.getAccountId(), account.getAccountName(),
						account.isStatus()));
			}
			log.info("Leaving getActiveAccountsByUser(). # of accounts {} for user {}", accountsDto.size(), userId);
			return accountsDto;
		} else {
			throw new NoSuchElementException("No active accounts available for user " + userId);
		}
	}

	@Transactional
	public List<AccountDto> getAllAccountsByUser(String userId) {
		log.info("Entering getAllAccountsByUser() for userId: {}", userId);
		Users user = new Users();
		user.setId(Long.valueOf(userId));
		log.info("Leaving getAllAccountsByUser() for userId: {}", userId);
		Set<Accounts> accounts = accountRepository.findByUsers(user);
		if (accounts != null && accounts.size() > 0) {
			List<AccountDto> accountsDto = new LinkedList<>();
			for (Accounts account : accounts) {
				accountsDto.add(new AccountDto(account.getId(), account.getAccountId(), account.getAccountName(),
						account.isStatus()));
			}
			log.info("Leaving getActiveAccountsByUser(). # of accounts {} for user {}", accountsDto.size(), userId);
			return accountsDto;
		} else {
			throw new NoSuchElementException("No accounts available for user " + userId);
		}
	}

	@Transactional
	public AccountDto deactivateAccount(String accountId) throws UserServiceException {
		log.info("Entering deactivateAccount() for accountId: {}", accountId);
		Accounts account = accountRepository.findById(Long.valueOf(accountId))
				.orElseThrow(() -> new NullPointerException("Account not found :: " + accountId));
		account.setStatus(false);
		final Accounts inactiveAccount = accountRepository.save(account);
		log.info("Leaving deactivateAccount() for accountId: {}", accountId);
		return new AccountDto(inactiveAccount.getId(), inactiveAccount.getAccountId(), inactiveAccount.getAccountName(),
				inactiveAccount.isStatus());
	}

}
