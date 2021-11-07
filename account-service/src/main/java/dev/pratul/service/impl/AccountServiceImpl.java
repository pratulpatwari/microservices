package dev.pratul.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.MDC;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import dev.pratul.config.ApiServices;
import dev.pratul.config.Constants;
import dev.pratul.dao.AccountRepository;
import dev.pratul.dto.AccountDto;
import dev.pratul.dto.UserDto;
import dev.pratul.entity.Account;
import dev.pratul.entity.User;
import dev.pratul.entity.UserAccount;
import dev.pratul.exception.UserServiceException;
import dev.pratul.model.AccountMapper;
import dev.pratul.model.Queries;
import dev.pratul.service.api.AccountService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

	private final AccountRepository accountRepository;
	private final ApiServices apiService;
	private final RestTemplate restTemplate;
	private final JdbcTemplate jdbcTemplate;

	private String accountNotFound = "Account not found";

	public AccountServiceImpl(AccountRepository accountRepository, ApiServices apiService,
			RestTemplate restTemplate, JdbcTemplate jdbcTemplate) {
		this.accountRepository = accountRepository;
		this.apiService = apiService;
		this.restTemplate = restTemplate;
		this.jdbcTemplate = jdbcTemplate;
	}

	@Transactional
	public AccountDto getAccountById(long id) {
		log.debug("Entering getAccountById() {}", id);
		Account account = accountRepository.findById(id)
				.orElseThrow(() -> new NoSuchElementException(accountNotFound));
		log.debug("Leaving getAccountById() {}", id);
		return new AccountDto(account.getId(), account.getAccountId(), account.getAccountName(),
				account.isStatus());
	}

	@Transactional
	public AccountDto getAccountDetailsById(long id) {
		log.debug("Entering getAccountDetailsById() with account id: {}", id);
		AccountDto account = jdbcTemplate.queryForObject(Queries.GET_ACCOUNT_DETAILS, new AccountMapper(), id);
		if (account != null) {
			log.debug("Leaving getAccountDetailsById() with account id: {} and # of user: {}", id,
					account.getUsers().size());

			return account;
		} else {
			throw new NoSuchElementException(accountNotFound);
		}
	}

	@Transactional
	public List<AccountDto> getAllAccountsByUser(long userId) {
		log.debug("Entering getAllAccountsByUser() for userId: {}", userId);
		List<AccountDto> accounts = jdbcTemplate.query(Queries.GET_ACCOUNTS_BY_USER, new AccountMapper(),
				userId);
		log.debug("Leaving getAllAccountsByUser() for userId: {} with # of accounts: {}", userId,
				accounts.size());
		return accounts;
	}

	@Transactional
	public List<AccountDto> deactivateAccount(@NonNull String accounts) {
		String[] accArr = accounts.split(",");
		log.debug("Entering deactivateAccount() for # of accounts: {}", accArr.length);
		Set<Long> accountIds = new HashSet<>();
		for (int i = 0; i < accArr.length; i++) {
			try {
				accountIds.add(Long.valueOf(accArr[i]));
			} catch (NumberFormatException ex) {
				log.error("Could not parse the account string: {}", ex.getMessage());
			}
		}
		if (accountIds.isEmpty()) {
			throw new IllegalArgumentException("Invalid accounts");
		}
		List<AccountDto> accountDtos = new LinkedList<>();
		Set<Account> accountList = accountRepository.findByIdIn(accountIds);
		if (accountList.isEmpty()) {
			throw new IllegalArgumentException(accountNotFound);
		}
		for (Account account : accountList) {
			// deactivate account for all users when account is being deactivate
			if (account.isStatus()) {
				account.getUserAccount().stream().forEach(acc -> acc.setStatus(false));
			}
			account.setStatus(!account.isStatus());
		}
		final List<Account> updatedAccounts = accountRepository.saveAll(accountList);
		for (Account account : updatedAccounts) {
			accountDtos.add(new AccountDto(account.getId(), account.getAccountId(),
					account.getAccountName(), account.isStatus(), null));
		}
		log.debug("Leaving deactivateAccount() for accountId: {}");
		return accountDtos;

	}

	private void userAccountsMapping(Account account, List<UserDto> userDtos, boolean status) {
		List<UserAccount> userAccount = new ArrayList<>();
		for (UserDto userDto : userDtos) {
			account.getUserAccount().stream()
					.filter(u -> u.getUser().getId().longValue() == userDto.getId().longValue())
					.findAny().ifPresentOrElse(u -> {
						UserDto userDtoById = getUserById(userDto.getId().longValue());
						log.debug("User Object: ", userDtoById);
						u.setStatus(status);
					}, () -> {
						UserDto userDtoById = getUserById(userDto.getId());
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
	@Transactional(readOnly = false)
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
					accountRepository.save(account);
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
		String url = apiService.getUser() + "/" + userId;
		HttpHeaders headers = new HttpHeaders();
		headers.set(Constants.CORRELATION_ID_HEADER_NAME, MDC.get(Constants.CORRELATION_ID_LOG_VAR_NAME));
		HttpEntity<UserDto> entity = new HttpEntity<>(headers);
		ResponseEntity<UserDto> respEntity = restTemplate.exchange(url, HttpMethod.GET, entity, UserDto.class);
		if (respEntity.getStatusCode() == HttpStatus.OK) {
			return respEntity.getBody();
		} else {
			log.error("User {} not found", userId);
		}
		return null;
	}

	private UserDto[] getUsersById(long[] userId) {
		String url = apiService.getUser() + "/list";
		HttpHeaders headers = new HttpHeaders();
		headers.set(Constants.CORRELATION_ID_HEADER_NAME, MDC.get(Constants.CORRELATION_ID_LOG_VAR_NAME));
		HttpEntity<UserDto[]> entity = new HttpEntity<>(headers);
		ResponseEntity<UserDto[]> respEntity = restTemplate.postForEntity(url, entity, UserDto[].class);
		if (respEntity.getStatusCode() == HttpStatus.OK) {
			return respEntity.getBody();
		} else {
			log.error("User {} not found", userId);
		}
		return new UserDto[0];
	}

	@HystrixCommand(fallbackMethod = "getAccountFromCache", commandKey = "accountService")
	@Transactional
	public AccountDto addAccount(AccountDto accountDto) {
		log.debug("Entering addAccount() with details: {}", accountDto.toString());
		UserAccount[] userAccounts = null;
		long[] users = accountDto.getUsers().stream().mapToLong(UserDto::getId).toArray();
		UserDto[] userDto = getUsersById(users);
		if (userDto != null && userDto.length > 0) {
			userAccounts = new UserAccount[userDto.length];
			for (int i = 0; i < userDto.length; i++) {
				userAccounts[i] = new UserAccount(new User(userDto[i].getId()), true);
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
							.collect(Collectors.toList()) : new ArrayList<>());

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
