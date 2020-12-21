package dev.pratul.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import dev.pratul.ApiServices;
import dev.pratul.UserServiceException;
import dev.pratul.dao.AccountRepository;
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
	private final ApiServices apiService;
	private final RestTemplate restTemplate;

	private String accountNotFound = "Account not found";

	public AccountServiceImpl(AccountRepository accountRepository, ApiServices apiService, RestTemplate restTemplate) {
		this.accountRepository = accountRepository;
		this.apiService = apiService;
		this.restTemplate = restTemplate;
	}

	@Transactional
	public AccountDto getAccountById(String id) {
		log.debug("Entering getAccountById() {}", id);
		if (id == null) {
			throw new IllegalArgumentException("No account provided");
		}
		Long accId = null;
		try {
			accId = Long.valueOf(id);
		} catch (Exception ex) {
			throw new IllegalArgumentException("Invalid account provided");
		}
		Account account = accountRepository.findById(accId)
				.orElseThrow(() -> new NullPointerException(accountNotFound));
		log.debug("Leaving getAccountById() {}", id);
		return new AccountDto(account.getId(), account.getAccountId(), account.getAccountName(), account.isStatus(),
				null);
	}

	@Transactional
	public AccountDto getAccountDetailsById(String id) {
		log.debug("Entering getAccountDetailsById() {}", id);
		Long accId = null;
		try {
			accId = Long.valueOf(id);
		} catch (Exception ex) {
			log.error("Error while parsing the accountId: {}", ex.getMessage());
			throw new IllegalArgumentException("No account provided");
		}
		Account account = accountRepository.findById(accId)
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

	@Transactional
	public List<AccountDto> getAllAccountsByUser(String userId) {
		log.debug("Entering getAllAccountsByUser() for userId: {}", userId);
		Long user = null;
		try {
			user = Long.valueOf(userId);
		} catch (Exception ex) {
			log.error("Exception while parsing the userId: {}", ex.getMessage());
			throw new IllegalArgumentException("Invalid user");
		}
		Set<Account> accounts = accountRepository.findByUser(new User(user));
		if (accounts == null || accounts.isEmpty()) {
			log.error("No accounts available for user: {}", userId);
			throw new NoSuchElementException("No accounts available for user " + userId);
		} else {
			List<AccountDto> dto = new LinkedList<>();
			for (Account acc : accounts) {
				boolean status = false;
				Optional<UserAccount> userAccount = acc.getUserAccount().stream().findFirst();
				if (!userAccount.isPresent()) {
					log.debug("No user assigned to this account");
				} else {
					status = userAccount.get().isStatus();
				}
				dto.add(new AccountDto(acc.getId(), acc.getAccountId(), acc.getAccountName(), status, null));
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
		List<UserAccount> userAccount = new LinkedList<>();
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
			if (accDto != null && !accDto.getUsers().isEmpty()) {
				Account updatedAccount = null;
				try {
					userAccountsMapping(account, accDto.getUsers(), accDto.isStatus());
					updatedAccount = accountRepository.save(account);
				} catch (IllegalArgumentException ex) {
					log.error("Error while saving the account: {}. Exception: {}", account.getAccountId(),
							ex.getMessage());
				}
				if (updatedAccount != null) {
					AccountDto acc = new AccountDto(updatedAccount.getId(), updatedAccount.getAccountId(),
							updatedAccount.getAccountName(), updatedAccount.isStatus(),
							updatedAccount.getUser() != null
									? updatedAccount.getUserAccount().stream().filter(UserAccount::isStatus)
											.map(u -> new UserDto(u.getUser().getId(), u.getUser().getFirstName(),
													u.getUser().getMiddleInitial(), u.getUser().getStatus(),
													u.getUser().getLastName(), null))
											.collect(Collectors.toList())
									: null);
					result.add(acc);
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
}
