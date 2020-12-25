package dev.pratul.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import dev.pratul.ApiServices;
import dev.pratul.UserServiceException;
import dev.pratul.dao.AccountRepository;
import dev.pratul.dao.UserAccountRepository;
import dev.pratul.dto.AccountDto;
import dev.pratul.dto.UserDto;
import dev.pratul.entity.Account;
import dev.pratul.entity.User;
import dev.pratul.entity.UserAccount;
import dev.pratul.service.api.AccountService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

	private final AccountRepository accountRepository;
	private final UserAccountRepository userAccountRepository;
	private final ApiServices apiService;
	private final RestTemplate restTemplate;

	private String accountNotFound = "Account not found";

	public AccountServiceImpl(AccountRepository accountRepository, UserAccountRepository userAccountRepository,
			ApiServices apiService, RestTemplate restTemplate) {
		this.accountRepository = accountRepository;
		this.userAccountRepository = userAccountRepository;
		this.apiService = apiService;
		this.restTemplate = restTemplate;
	}

	@Transactional
	public AccountDto getAccountById(String id) {
		log.debug("Entering getAccountById() {}", id);
		if (id == null) {
			throw new IllegalArgumentException("No account provided");
		}
		Account account = accountRepository.findById(Long.valueOf(id))
				.orElseThrow(() -> new NullPointerException(accountNotFound));
		log.debug("Leaving getAccountById() {}", id);
		return new AccountDto(account.getId(), account.getAccountId(), account.getAccountName(), account.isStatus(),
				null);
	}

	@Transactional
	public AccountDto getAccountDetailsById(String id) {
		log.debug("Entering getAccountDetailsById() {}", id);
		Account account = accountRepository.findById(Long.valueOf(id))
				.orElseThrow(() -> new NullPointerException(accountNotFound));
		AccountDto accountDto = new AccountDto(account.getId(), account.getAccountId(), account.getAccountName(),
				account.isStatus());
		for (UserAccount userAccount : account.getUserAccount()) {
			try {
				if (userAccount.isStatus()) {
					User user = userAccount.getUser();
					accountDto.getUsers().add(new UserDto(user.getId(), user.getFirstName(), user.getMiddleInitial(),
							user.getStatus(), user.getLastName(), user.getEmail()));
				}
			} catch (Exception ex) {
				log.error("Exception while reading the user account ID: {}. Exception: {}", userAccount.getId(),
						ex.getMessage());
			}
		}
		log.debug("Leaving getAccountDetailsById() {} with # of users: {}", id, accountDto.getUsers().size());
		return accountDto;
	}

	@Transactional
	public List<AccountDto> getActiveAccountsByUser(String userId) {
		log.debug("Entering getActiveAccountsByUser() for userId: {}", userId);
		UserDto userDto = null;
		try {
			userDto = getUserById(Long.valueOf(userId));
		} catch (Exception ex) {
			log.error("Exception while fetching the user:");
		}
		if (userDto == null) {
			throw new UserServiceException("Could not find the requested user");
		}
		Set<Account> accounts = accountRepository.findByUserIdAndStatusTrueAndUserAccountStatusTrue(userDto.getId());
		if (accounts.isEmpty()) {
			log.error("No accounts available for user: {}", userId);
			throw new NoSuchElementException("No accounts available for user " + userId);
		} else {
			List<AccountDto> dto = new LinkedList<>();
			for (Account acc : accounts) {
				dto.add(new AccountDto(acc.getId(), acc.getAccountId(), acc.getAccountName(), acc.isStatus(), null));
			}
			log.debug("Leaving getAllAccountsByUser(). # of accounts {} for user {}", dto.size(), userId);
			return dto;
		}
	}

	/*
	 * In order to prevent multiple queries to DB, we are querying on the status
	 * which will send only 2 separate queries depending upon the status and user
	 */
	@Transactional
	public List<AccountDto> getAllAccountsByUser(String userId) {
		log.debug("Entering getAllAccountsByUser() for userId: {}", userId);
		Set<Account> activeAccounts = accountRepository
				.findByUserIdAndStatusTrueAndUserAccountStatus(Long.valueOf(userId), true);
		Set<Account> deactiveAccounts = accountRepository
				.findByUserIdAndStatusTrueAndUserAccountStatus(Long.valueOf(userId), false);

		if (activeAccounts.isEmpty() && deactiveAccounts.isEmpty()) {
			log.error("No accounts available for user: {}", userId);
			throw new NoSuchElementException("No accounts available for user " + userId);
		} else {
			List<AccountDto> dto = new LinkedList<>();
			for (Account acc : activeAccounts) {
				dto.add(new AccountDto(acc.getId(), acc.getAccountId(), acc.getAccountName(), true));
			}

			for (Account acc : deactiveAccounts) {
				if (acc.isStatus()) {
					dto.add(new AccountDto(acc.getId(), acc.getAccountId(), acc.getAccountName(), false));
				}
			}
			log.debug("Leaving getAllAccountsByUser(). # of accounts {} for user {}", dto.size(), userId);
			return dto;
		}
	}

	@Transactional
	public List<AccountDto> deactivateAccount(String accounts) {
		log.debug("Entering updateAccountStatus() for # of accounts: {}", accounts);
		if (accounts == null || accounts.isBlank()) {
			throw new IllegalArgumentException(accountNotFound);
		}
		String[] accArr = accounts.split(",");
		Set<Long> accountIds = new HashSet<>();
		for (int i = 0; i < accArr.length; i++) {
			try {
				accountIds.add(Long.valueOf(accArr[i]));
			} catch (NumberFormatException ex) {
				log.error("Could not parse the account string: {}", ex.getMessage());
			}
		}
		if (accountIds.isEmpty()) {
			throw new IllegalArgumentException("Invalid account");
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
			accountDtos.add(new AccountDto(account.getId(), account.getAccountId(), account.getAccountName(),
					account.isStatus(), null));
		}
		log.debug("Leaving updateAccountStatus() for accountId: {}");
		return accountDtos;

	}

	private void userAccountsMapping(Account account, List<UserDto> userDtos, boolean status) {
		List<UserAccount> userAccount = new ArrayList<>();
		for (UserDto userDto : userDtos) {
			account.getUserAccount().stream()
					.filter(u -> u.getUser().getId().longValue() == userDto.getId().longValue()).findAny()
					.ifPresentOrElse(u -> {
						UserDto userDtoById = getUserById(userDto.getId());
						log.debug("User Object: ", userDtoById);
						u.setStatus(status);
					}, () -> {
						UserDto userDtoDtoById = getUserById(userDto.getId());
						if (userDtoDtoById != null) {
							User user = new User();
							user.setFirstName(userDtoDtoById.getFirstName());
							user.setLastName(userDtoDtoById.getLastName());
							user.setMiddleInitial(userDtoDtoById.getMiddleInitial());
							user.setStatus(userDtoDtoById.getStatus());
							userAccount.add(new UserAccount(user, true));
						} else {
							log.debug(
									"User {} not found. User account map will be skipped, rest other accounts will be processed",
									userDto.getId());
						}
					});
		}
		account.getUserAccount().addAll(userAccount);
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
			if (accDto != null && !accDto.getUsers().isEmpty()) {
				try {
					if (account.isStatus()) {
						userAccountsMapping(account, accDto.getUsers(), accDto.isStatus());
						accountRepository.save(account);
					}
				} catch (IllegalArgumentException ex) {
					log.error("Error while saving the account: {}. Exception: {}", account.getAccountId(),
							ex.getMessage());
				}
			}
		}
		return result;
	}

	private UserDto getUserById(Long userId) {
		String url = apiService.getUser() + "user/" + userId;
		ResponseEntity<UserDto> user = restTemplate.getForEntity(url, UserDto.class);
		if (user.getStatusCode() == HttpStatus.OK) {
			return user.getBody();
		} else {
			log.error("User {} not found", userId);
		}
		return null;
	}

	@HystrixCommand(fallbackMethod = "getAccountFromCache", commandKey = "accountService")
	@Transactional
	public AccountDto addAccount(AccountDto accountDto) {
		log.debug("Entering addAccount() with details: {}", accountDto.toString());
		UserAccount[] userAccounts = null;
		String url = apiService.getUser() + "/user/list";
		List<Long> userIds = accountDto.getUsers().stream().map(UserDto::getId).collect(Collectors.toList());
		ResponseEntity<UserDto[]> userDtos = restTemplate.postForEntity(url, userIds, UserDto[].class);
		if (userDtos.getStatusCode() == HttpStatus.OK) {
			UserDto[] userDto = userDtos.getBody();
			if (userDto != null && userDto.length > 0) {
				userAccounts = new UserAccount[userDto.length];
				for (int i = 0; i < userDto.length; i++) {
					userAccounts[i] = new UserAccount(new User(userDto[i].getId()), true);
				}
			}
		}
		Account account = new Account(accountDto.getAccountId(), accountDto.getAccountName(),
				userAccounts != null ? userAccounts : null);
		try {
			account = accountRepository.save(account);
			if (account.getId() != null) {
				log.debug("Leaving addAccount() with Id: {}", accountDto.getId());
				return new AccountDto(account.getId(), account.getAccountId(), account.getAccountName(),
						account.isStatus(),
						account.getUserAccount() != null && !account.getUserAccount().isEmpty()
								? account.getUserAccount().stream()
										.map(acc -> new UserDto(acc.getUser().getId(), acc.getUser().getFirstName(),
												acc.getUser().getMiddleInitial(), acc.getUser().getStatus(),
												acc.getUser().getLastName(), acc.getUser().getEmail()))
										.collect(Collectors.toList())
								: null);
			}
		} catch (Exception ex) {
			log.error("Error while saving the accounts: {}", ex.getMessage());
			throw new UserServiceException("Account could not be assigned to the user. Please contact admin !");
		}
		return null;
	}

	public List<AccountDto> getAccountFromCache(List<AccountDto> accountDtos) {
		log.debug("Entering getUserFromCache with accounts: {}", accountDtos.size());
		return new ArrayList<>();
	}

	@Transactional
	public void addAccounts() {
		List<Account> accounts = new LinkedList<>();
		for (int i = 324217; i <= 325217; i++) {
			accounts.add(new Account(String.valueOf(i), "Domestic account"));
		}
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
		StringBuilder sb = new StringBuilder(8);
		for (int i = 0; i < 1000; i++) {
			for (int j = 0; j < 8; j++) {
				int index = (int) (AlphaNumericString.length() * Math.random());
				sb.append(AlphaNumericString.charAt(index));
			}
			accounts.add(new Account(sb.toString(), "Global Account"));
			sb.delete(0, 8);
		}
		accountRepository.saveAll(accounts);
	}

	@Transactional
	public void addUserAccount() {
		String url = apiService.getUser() + "user/all";
		ResponseEntity<UserDto[]> user = restTemplate.getForEntity(url, UserDto[].class);
		List<Account> accounts = accountRepository.findAll();
		if (user.getStatusCode() == HttpStatus.OK) {
			UserDto[] users = user.getBody();
			for (int i = 0; i < users.length; i++) {
				for (int j = i; j < accounts.size(); j += 25 - i * 2) {
					accounts.get(j).getUserAccount()
							.add(new UserAccount(new User(users[i].getId()), accounts.get(j), true));
				}
			}
			accountRepository.saveAll(accounts);
		}
	}
}
