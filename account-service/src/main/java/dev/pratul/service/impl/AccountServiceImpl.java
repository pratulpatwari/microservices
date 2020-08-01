package dev.pratul.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import dev.pratul.UserServiceException;
import dev.pratul.dao.AccountRepository;
import dev.pratul.dao.UserAccountRepository;
import dev.pratul.dto.AccountDto;
import dev.pratul.dto.UserDto;
import dev.pratul.entity.Accounts;
import dev.pratul.entity.UserAccount;
import dev.pratul.entity.Users;
import dev.pratul.service.api.AccountService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

	private final AccountRepository accountRepository;
	private final UserAccountRepository userAccountRepository;
	private final RestTemplate restTemplate;

	public AccountServiceImpl(AccountRepository accountRepository, UserAccountRepository userAccountRepository,
			RestTemplate restTemplate) {
		this.accountRepository = accountRepository;
		this.userAccountRepository = userAccountRepository;
		this.restTemplate = restTemplate;
	}

	@Value("${service.uri.user}")
	private String userService;

	@Transactional
	public AccountDto getAccountById(String id) {
		log.debug("Entering getAccountById() {}", id);
		Accounts account = accountRepository.findById(Long.valueOf(id))
				.orElseThrow(() -> new NullPointerException("Account not found"));
		log.debug("Leaving getAccountById() {}", id);
		return new AccountDto(account.getId(), account.getAccountId(), account.getAccountName(), account.isStatus(),
				null);
	}

	@Transactional
	public List<AccountDto> getActiveAccountsByUser(String userId) {
		log.debug("Entering getActiveAccountsByUser() for userId: {}", userId);
		Users user = new Users();
		user.setId(Long.valueOf(userId));
		Set<Accounts> accounts = accountRepository.findByUserAndStatusTrueAndUserAccount_StatusTrue(user);
		if (accounts == null || accounts.isEmpty()) {
			log.error("No accounts available for user: {}", userId);
			throw new NoSuchElementException("No accounts available for user " + userId);
		} else {
			List<AccountDto> dto = new LinkedList<>();
			for (Accounts acc : accounts) {
				dto.add(new AccountDto(acc.getId(), acc.getAccountId(), acc.getAccountName(), acc.isStatus(), null));
			}
			log.debug("Leaving getAllAccountsByUser(). # of accounts {} for user {}", dto.size(), userId);
			return dto;
		}
	}

	@Transactional
	public List<AccountDto> getAllAccountsByUser(String userId) {
		log.debug("Entering getAllAccountsByUser() for userId: {}", userId);
		Users user = new Users();
		user.setId(Long.valueOf(userId));
		Set<Accounts> accounts = accountRepository.findByUser(user);
		if (accounts == null || accounts.isEmpty()) {
			log.error("No accounts available for user: {}", userId);
			throw new NoSuchElementException("No accounts available for user " + userId);
		} else {
			List<AccountDto> dto = new LinkedList<>();
			for (Accounts acc : accounts) {
				dto.add(new AccountDto(acc.getId(), acc.getAccountId(), acc.getAccountName(),
						acc.getUserAccount().stream().findFirst().get().isStatus(), null));
			}
			log.debug("Leaving getAllAccountsByUser(). # of accounts {} for user {}", dto.size(), userId);
			return dto;
		}
	}

	@Transactional
	public AccountDto deactivateAccount(String accountId) {
		log.debug("Entering deactivateAccount() for accountId: {}", accountId);

		Accounts account = accountRepository.findById(Long.valueOf(accountId))
				.orElseThrow(() -> new NullPointerException("Account not found :: " + accountId));

		// deactivate account for all users
		account.getUserAccount().stream().forEach(acc -> acc.setStatus(false));

		// deactivate the account in account table
		account.setStatus(false);

		final Accounts inactiveAccount = accountRepository.save(account);

		log.debug("Leaving deactivateAccount() for accountId: {}", accountId);
		return new AccountDto(inactiveAccount.getId(), inactiveAccount.getAccountId(), inactiveAccount.getAccountName(),
				inactiveAccount.isStatus(), null);
	}

	@Transactional
	public boolean deactivateUserAccount(String userId, String accountId) {
		log.debug("Entering deactivateUserAccount() for accountId and userId: {}, {}", accountId, userId);
		int row = userAccountRepository.deactivateUserAccount(Long.valueOf(userId), Long.valueOf(accountId));
		log.debug("Leaving deactivateUserAccount() for accountId and userId: {}, {}. # of rows impacted: {}", accountId,
				userId, row);
		return row == 1 ? true : false;
	}

	@HystrixCommand(fallbackMethod = "getUsersFromCache", commandKey = "accountService")
	@Transactional
	public AccountDto addAccount(AccountDto accountDto) {
		log.debug("Entering addAccount() with details: {}", accountDto.toString());
		UserAccount[] userAccounts = null;
		String url = userService + "/user/list";
		ResponseEntity<UserDto[]> userDtos = restTemplate.postForEntity(url, accountDto.getUserId(), UserDto[].class);
		if (userDtos.getStatusCode() == HttpStatus.OK) {
			userAccounts = new UserAccount[userDtos.getBody().length];
			for (int i = 0; i < userDtos.getBody().length; i++) {
				userAccounts[i] = new UserAccount(new Users(userDtos.getBody()[i].getId()), true);
			}
		}
		Accounts account = new Accounts(accountDto.getAccountId(), accountDto.getAccountName(),
				userAccounts != null ? userAccounts : null);
		try {
			account = accountRepository.save(account);
			if (account.getId() != null) {
				log.debug("Leaving addAccount() with Id: {}", accountDto.getId());
				return new AccountDto(account.getId(), account.getAccountId(), account.getAccountName(),
						account.isStatus(),
						account.getUserAccount() != null && account.getUserAccount().size() > 0
								? account.getUserAccount().stream().map(acc -> acc.getUsers().getId()).collect(
										Collectors.toList())
								: null);
			}
		} catch (Exception ex) {
			log.error("Error while saving the accounts: {}", ex.getMessage());
			throw new UserServiceException("Account not be created. Please contact admin !");
		}
		return null;
	}

	@SuppressWarnings("unused")
	private List<Users> getUserFromCache(String userId) {
		return null;
	}
}
