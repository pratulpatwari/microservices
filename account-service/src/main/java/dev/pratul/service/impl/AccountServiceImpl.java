package dev.pratul.service.impl;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import dev.pratul.dto.AccountDto;
import dev.pratul.dto.UserDto;
import dev.pratul.entity.Accounts;
import dev.pratul.entity.User;
import dev.pratul.entity.UserAccount;
import dev.pratul.service.api.AccountService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

	private final AccountRepository accountRepository;
	private final RestTemplate restTemplate;

	public AccountServiceImpl(AccountRepository accountRepository, RestTemplate restTemplate) {
		this.accountRepository = accountRepository;
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
	public AccountDto getAccountDetailsById(String id) {
		log.debug("Entering getAccountDetailsById() {}", id);
		Accounts account = accountRepository.findById(Long.valueOf(id))
				.orElseThrow(() -> new NullPointerException("Account not found"));
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
			throw new UserServiceException("User with ID " + userId + " does not exists");
		}
		Set<Accounts> accounts = accountRepository.findByUser_IdAndStatusTrueAndUserAccount_StatusTrue(userDto.getId());
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
		Set<Accounts> accounts = accountRepository.findByUser(new User(Long.valueOf(userId)));
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
	public List<AccountDto> updateAccountStatus(List<String> accountList) {
		log.debug("Entering updateAccountStatus() for # of accounts: {}", accountList.size());
		Set<Long> accountToLookup = accountList.stream().map(acc -> Long.valueOf(acc)).collect(Collectors.toSet());
		List<AccountDto> accountDtos = new LinkedList<>();
		Set<Accounts> accounts = accountRepository.findByIdIn(accountToLookup);
		if (accounts == null || accounts.isEmpty()) {
			throw new NullPointerException("Accounts not found");
		}
		for (Accounts account : accounts) {
			// deactivate account for all users when account is being deactivate
			if (account.isStatus()) {
				account.getUserAccount().stream().forEach(acc -> acc.setStatus(false));
			}
			account.setStatus(!account.isStatus());
		}
		final List<Accounts> updatedAccounts = accountRepository.saveAll(accounts);
		for (Accounts account : updatedAccounts) {
			accountDtos.add(new AccountDto(account.getId(), account.getAccountId(), account.getAccountName(),
					account.isStatus(), null));
		}
		log.debug("Leaving updateAccountStatus() for accountId: {}");
		return accountDtos;
	}

	@HystrixCommand(fallbackMethod = "getUserFromCache", commandKey = "accountService")
	@Transactional
	public List<AccountDto> updateUserAccount(List<AccountDto> accountDtos) {
		log.debug("Entering updateUserAccount() for userId and account: {}, {}", accountDtos.toString());
		List<AccountDto> result = new LinkedList<>();
		Map<String, AccountDto> accountMap = accountDtos.stream()
				.collect(Collectors.toMap(AccountDto::getAccountId, acc -> acc));
		Set<Accounts> accounts = accountRepository.findByAccountIdIn(accountMap.keySet());
		for (Accounts account : accounts) {
			AccountDto accDto = accountMap.get(account.getAccountId());
			Set<UserAccount> userAccount = new HashSet<>();
			if (accDto != null && !accDto.getUsers().isEmpty()) {
				for (UserDto user : accDto.getUsers()) {
					account.getUserAccount().stream().filter(u -> u.getUser().getId() == user.getId()).findAny()
							.ifPresentOrElse(u -> {
								UserDto userDto = getUserById(user.getId());
								log.debug("User Object: ", userDto);
								u.setStatus(accDto.isStatus());
							}, () -> {
								UserDto userDto = getUserById(user.getId());
								if (userDto != null) {
									User u = new User(userDto.getId());
									u.setFirstName(userDto.getFirstName());
									u.setLastName(userDto.getLastName());
									u.setMiddleInitial(userDto.getMiddleInitial());
									u.setStatus(userDto.getStatus());
									userAccount.add(new UserAccount(u, true));
								} else {
									log.debug(
											"User {} not found. User account map will be skipped, rest other accounts will be processed",
											user.getId());
								}
							});
				}
				if (!userAccount.isEmpty()) {
					account.getUserAccount().addAll(userAccount);
				}
				try {
					final Accounts updatedAccount = accountRepository.save(account);
					AccountDto acc = new AccountDto(updatedAccount.getId(), updatedAccount.getAccountId(),
							updatedAccount.getAccountName(), updatedAccount.isStatus(),
							updatedAccount.getUser() != null
									? updatedAccount.getUserAccount().stream().filter(ua -> ua.isStatus())
											.map(u -> new UserDto(u.getUser().getId(), u.getUser().getFirstName(),
													u.getUser().getMiddleInitial(), u.getUser().getStatus(),
													u.getUser().getLastName(), null))
											.collect(Collectors.toList())
									: null);
					result.add(acc);
				} catch (Exception ex) {
					log.error("Error while saving the account: {}. Exception: {}", account.getAccountId(),
							ex.getMessage());
				}
			} else {
				log.error("Account {} you are trying to assign users is not found", account.getAccountId());
			}
		}
		return result;
	}

	private UserDto getUserById(Long userId) {
		String url = userService + "user/" + userId;
		ResponseEntity<UserDto> user = restTemplate.getForEntity(url, UserDto.class);
		if (user.getStatusCode() == HttpStatus.OK) {
			return user.getBody();
		} else {
			log.error("User {} not found", userId);
		}
		return null;
	}

	@HystrixCommand(fallbackMethod = "getUserFromCache", commandKey = "accountService")
	@Transactional
	public AccountDto addAccount(AccountDto accountDto) {
		log.debug("Entering addAccount() with details: {}", accountDto.toString());
		UserAccount[] userAccounts = null;
		String url = userService + "/user/list";
		List<Long> userIds = accountDto.getUsers().stream().map(u -> u.getId()).collect(Collectors.toList());
		ResponseEntity<UserDto[]> userDtos = restTemplate.postForEntity(url, userIds, UserDto[].class);
		if (userDtos.getStatusCode() == HttpStatus.OK) {
			userAccounts = new UserAccount[userDtos.getBody().length];
			for (int i = 0; i < userDtos.getBody().length; i++) {
				userAccounts[i] = new UserAccount(new User(userDtos.getBody()[i].getId()), true);
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

	public List<AccountDto> getUserFromCache(List<AccountDto> accountDtos) {
		return null;
	}
}
