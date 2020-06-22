package dev.pratul.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.pratul.dao.AccountRepository;
import dev.pratul.dao.UserAccountRepository;
import dev.pratul.dto.AccountDto;
import dev.pratul.entity.Accounts;
import dev.pratul.entity.UserAccount;
import dev.pratul.entity.Users;
import dev.pratul.service.api.AccountService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private UserAccountRepository userAccountRepository;

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
		Set<Accounts> accounts = accountRepository.findByUser(user);
		List<UserAccount> userAccount = userAccountRepository.findByUsersAndStatusTrueAndAccountsIn(user, accounts);
		if (accounts == null || accounts.isEmpty() || userAccount == null || userAccount.isEmpty()) {
			log.error("No accounts available for user: {}", userId);
			throw new NoSuchElementException("No accounts available for user " + userId);
		} else {
			List<AccountDto> dto = new LinkedList<>();
			for (UserAccount acc : userAccount) {
				dto.add(new AccountDto(acc.getAccounts().getId(), acc.getAccounts().getAccountId(),
						acc.getAccounts().getAccountName(), acc.isStatus()));
			}
			log.info("Leaving getAllAccountsByUser(). # of accounts {} for user {}", dto.size(), userId);
			return dto;
		}
	}

	@Transactional
	public List<AccountDto> getAllAccountsByUser(String userId) {
		log.info("Entering getAllAccountsByUser() for userId: {}", userId);
		Users user = new Users();
		user.setId(Long.valueOf(userId));
		Set<Accounts> accounts = accountRepository.findByUser(user);
		List<UserAccount> userAccount = userAccountRepository.findByUsersAndAccountsIn(user, accounts);
		if (accounts == null || accounts.isEmpty() || userAccount == null || userAccount.isEmpty()) {
			log.error("No accounts available for user: {}", userId);
			throw new NoSuchElementException("No accounts available for user " + userId);
		} else {
			List<AccountDto> dto = new LinkedList<>();
			for (UserAccount acc : userAccount) {
				dto.add(new AccountDto(acc.getAccounts().getId(), acc.getAccounts().getAccountId(),
						acc.getAccounts().getAccountName(), acc.isStatus()));
			}
			log.info("Leaving getAllAccountsByUser(). # of accounts {} for user {}", dto.size(), userId);
			return dto;
		}
	}

	@Transactional
	public AccountDto deactivateAccount(String accountId) {
		log.info("Entering deactivateAccount() for accountId: {}", accountId);

		Accounts account = accountRepository.findById(Long.valueOf(accountId))
				.orElseThrow(() -> new NullPointerException("Account not found :: " + accountId));

		List<UserAccount> userAccounts = userAccountRepository.findByAccounts(account);

		// deactivate account for all users
		userAccounts.stream().forEach(u -> u.setStatus(false));
		userAccountRepository.saveAll(userAccounts);

		// deactivate the account in account table
		account.setStatus(false);
		final Accounts inactiveAccount = accountRepository.save(account);

		log.info("Leaving deactivateAccount() for accountId: {}", accountId);
		return new AccountDto(inactiveAccount.getId(), inactiveAccount.getAccountId(), inactiveAccount.getAccountName(),
				inactiveAccount.isStatus());
	}

	@Transactional
	public boolean deactivateUserAccount(String userId, String accountId) {
		log.info("Entering deactivateUserAccount() for accountId and userId: {}, {}", accountId, userId);
		int row = userAccountRepository.deactivateUserAccount(Long.valueOf(userId), Long.valueOf(accountId));
		log.info("Leaving deactivateUserAccount() for accountId and userId: {}, {}. # of rows impacted: {}", accountId,
				userId, row);
		return row == 1 ? true : false;
	}

}
