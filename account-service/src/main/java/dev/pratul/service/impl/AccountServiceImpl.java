package dev.pratul.service.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import org.slf4j.MDC;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import dev.pratul.config.ApiServices;
import dev.pratul.config.Constants;
import dev.pratul.dao.AccountRepository;
import dev.pratul.dto.AccountDto;
import dev.pratul.dto.UserDto;
import dev.pratul.entity.Account;
import dev.pratul.entity.User;
import dev.pratul.entity.UserAccount;
import dev.pratul.exception.UserServiceException;
import dev.pratul.repository.AccountMapper;
import dev.pratul.repository.Queries;
import dev.pratul.service.api.AccountService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

	private final AccountRepository accountRepository;
	private final ApiServices apiService;
	private final JdbcTemplate jdbcTemplate;
	private final WebClient.Builder webClientBuilder;

	private String accountNotFound = "Account not found";

	public AccountServiceImpl(AccountRepository accountRepository, ApiServices apiService,
			JdbcTemplate jdbcTemplate, WebClient.Builder webClientBuilder) {
		this.accountRepository = accountRepository;
		this.apiService = apiService;
		this.webClientBuilder = webClientBuilder;
		this.jdbcTemplate = jdbcTemplate;
	}

	@Transactional(readOnly = true)
	public AccountDto getAccountById(long id) {
		log.debug("Entering getAccountById() {}", id);
		Account account = accountRepository.findById(id).orElseThrow(() -> new NoSuchElementException(accountNotFound));
		return new AccountDto(account.getId(), account.getAccountId(), account.getAccountName(),
				account.isStatus());
	}

	@Transactional(readOnly = true)
	public AccountDto getAccountDetailsById(long id) {
		log.debug("Entering getAccountDetailsById() with account id: {}", id);
		AccountDto account = jdbcTemplate.queryForObject(Queries.GET_ACCOUNT_DETAILS, new AccountMapper(), id);
		if (account == null) {
			throw new NoSuchElementException(accountNotFound);
		}
		log.debug("Leaving getAccountDetailsById() with account id: {} and # of user: {}", id,
				account.getUsers().size());

		return account;
	}

	@Transactional(readOnly = true)
	public List<AccountDto> getAllAccountsByUser(long userId) {
		log.debug("Entering getAllAccountsByUser() for userId: {}", userId);
		List<AccountDto> accounts = jdbcTemplate.query(Queries.GET_ACCOUNTS_BY_USER, new AccountMapper(),
				userId);
		log.debug("Leaving getAllAccountsByUser() for userId: {} with # of accounts: {}", userId,
				accounts.size());
		return accounts;
	}

	@Transactional
	public boolean deactivateAccount(long accountId) {
		log.debug("Entering deactivateAccount() for accounts: {}", accountId);
		Account account = accountRepository.findById(accountId)
					.orElseThrow(() -> new NoSuchElementException("Invalid account"));
					account.getUserAccount().stream().forEach(acc -> acc.setStatus(false));
			account.setStatus(false);
		try {	
			accountRepository.save(account);
			log.debug("Leaving deactivateAccount() for account {}", accountId);
		} catch (IllegalArgumentException e) {
			log.error("Exception while deactivating the account", e.getMessage());
			throw new IllegalArgumentException("Could not deactivate account. Please try again");
		}
		return true;
	}

	private void userAccountsMapping(Account account, List<UserDto> userDtos, boolean status) {
		List<UserAccount> userAccount = new ArrayList<>();
		for (UserDto userDto : userDtos) {
			UserDto userDtoById = getUserById(userDto.getId().longValue());
			log.debug("User Object: ", userDtoById);
			account.getUserAccount().stream()
					.filter(u -> u.getUser().getId().equals(userDto.getId()))
					.findAny().ifPresentOrElse(u -> {
						u.setStatus(status);
					}, () -> {
						if (userDtoById != null) {
							User user = new User();
							user.setFirstName(userDtoById.getFirstName());
							user.setLastName(userDtoById.getLastName());
							user.setMiddleInitial(userDtoById.getMiddleInitial());
							user.setStatus(userDtoById.getStatus());
							userAccount.add(new UserAccount(user, true));
						} else {
							log.debug("User {} not found. User account map will be skipped, rest other accounts will be processed",
									userDto.getId());
						}
					});
		}
		if (!userAccount.isEmpty()) {
			account.getUserAccount().addAll(userAccount);
		}
	}

	@HystrixCommand(fallbackMethod = "getAccountFromCache", commandKey = "accountService")
	@Transactional
	public List<AccountDto> updateUserAccount(List<AccountDto> accountDtos) {
		log.debug("Entering updateUserAccount() with # of accounts: {}", accountDtos.size());
		List<AccountDto> result = new LinkedList<>();
		Map<String, AccountDto> accountMap = accountDtos.stream()
				.collect(Collectors.toMap(AccountDto::getAccountId, acc -> acc));
		Set<Account> accounts = accountRepository.findByAccountIdIn(accountMap.keySet());
		for (Account account : accounts) {
			AccountDto accDto = accountMap.get(account.getAccountId());
			if (accDto != null && !accDto.getUsers().isEmpty() && account.isStatus()) {
				userAccountsMapping(account, accDto.getUsers(), accDto.isStatus());
				try {
					account = accountRepository.save(account);
					result.add(new AccountDto(account.getId(), account.getAccountId(), account.getAccountName(), account.isStatus()));
				} catch (IllegalArgumentException ex) {
					log.error("Error while saving the account: {}. Exception: {}",
							account.getAccountId(), ex.getMessage());
				}
			}
		}
		log.debug("Leaving updateUserAccount() with # of accounts: {}", accountDtos.size());
		return result;
	}

	private UserDto getUserById(long userId) {
		String url = apiService.getUser() + userId;
		return webClientBuilder.build().get().uri(url)
				.header(Constants.CORRELATION_ID_HEADER_NAME,
						MDC.get(Constants.CORRELATION_ID_LOG_VAR_NAME))
				.retrieve().bodyToMono(UserDto.class).block();
	}

	private List<UserDto> getUsersById(long[] userId) {
		String url = apiService.getUser() + "/list";
		return webClientBuilder.build().get().uri(url)
				.header(Constants.CORRELATION_ID_HEADER_NAME,
						MDC.get(Constants.CORRELATION_ID_LOG_VAR_NAME))
				.retrieve().bodyToFlux(UserDto.class).collectList().block();
	}

	@HystrixCommand(fallbackMethod = "getAccountFromCache", commandKey = "accountService")
	@Transactional
	public AccountDto addAccount(AccountDto accountDto) {
		log.debug("Entering addAccount() with details: {}", accountDto.toString());
		UserAccount[] userAccounts = null;
		long[] users = accountDto.getUsers().stream().mapToLong(UserDto::getId).toArray();
		List<UserDto> userDto = getUsersById(users);
		if (userDto != null && !userDto.isEmpty()) {
			userAccounts = new UserAccount[userDto.size()];
			for (int i = 0; i < userDto.size(); i++) {
				userAccounts[i] = new UserAccount(new User(userDto.get(i).getId()), true);
			}
		}
		Account account = new Account(accountDto.getAccountId(), accountDto.getAccountName(),
				userAccounts != null ? userAccounts : null);
		try {
			account = accountRepository.save(account);
			log.debug("Leaving addAccount() with Id: {}", accountDto.getId());
			return new AccountDto(account.getId(), account.getAccountId(), account.getAccountName(),
					account.isStatus(),
					!account.getUserAccount().isEmpty() ? account.getUserAccount().stream()
							.map(acc -> new UserDto(acc.getUser().getId(),
									acc.getUser().getFirstName(),
									acc.getUser().getMiddleInitial(),
									acc.getUser().getStatus(),
									acc.getUser().getLastName(),
									acc.getUser().getEmail(), null))
							.collect(Collectors.toList()) : List.of());

		} catch (Exception ex) {
			log.error("Error while saving the accounts: {}", ex.getMessage());
			throw new UserServiceException(
					"Account could not be assigned to the user. Please contact admin !");
		}
	}

	public List<AccountDto> getAccountFromCache(List<AccountDto> accountDtos) {
		log.debug("Entering getUserFromCache with accounts: {}", accountDtos.size());
		return new ArrayList<>();
	}
}
