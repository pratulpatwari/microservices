package dev.pratul.service.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import dev.pratul.UserServiceException;
import dev.pratul.dao.AccountRepository;
import dev.pratul.dto.AccountDto;
import dev.pratul.dto.UserDto;
import dev.pratul.entity.Accounts;
import dev.pratul.entity.User;
import dev.pratul.entity.UserAccount;
import dev.pratul.service.api.AccountService;

@SpringBootTest
class AccountServiceImplTest {

	@Autowired
	private AccountService accountService;

	@MockBean
	private AccountRepository accountRepository;

	@MockBean
	RestTemplate restTemplate;

	Set<Accounts> accounts = new HashSet<>();
	Accounts account = new Accounts();
	Accounts deactiveAccount = new Accounts();
	User user = new User();
	UserAccount userAccount = new UserAccount();

	@BeforeEach
	public void setupRepository() {
		user.setId(1L);

		account.setId(1L);
		account.setStatus(true);
		account.setAccountId("324567");
		account.setUser(Stream.of(user).collect(Collectors.toSet()));
		userAccount.setAccounts(account);
		userAccount.setUser(user);
		userAccount.setStatus(true);
		account.setUserAccount(Stream.of(userAccount).collect(Collectors.toSet()));

		deactiveAccount.setId(2L);
		deactiveAccount.setStatus(false);
		deactiveAccount.setAccountId("543232");

		accounts.add(account);
		accounts.add(deactiveAccount);
	}

	@Test
	void testGetAccountById() {
		Mockito.when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
		AccountDto result = accountService.getAccountById(String.valueOf(account.getId()));
		assertEquals(1, result.getId());
		assertTrue(result.isStatus());
		assertThrows(NullPointerException.class, () -> {
			accountService.getAccountById("2");
		});
		assertThrows(IllegalArgumentException.class, () -> {
			accountService.getAccountById(null);
		});
		assertThrows(IllegalArgumentException.class, () -> {
			accountService.getAccountById("abc");
		}, "Invalid account provided");
	}

	@Test
	void testAccountDetailsById() {
		assertThrows(IllegalArgumentException.class, () -> {
			accountService.getAccountDetailsById(null);
		}, "No account provided");
		Mockito.when(accountRepository.findById(Mockito.anyLong())).thenReturn(null);
		assertThrows(NullPointerException.class, () -> {
			accountService.getAccountDetailsById("1");
		}, "No account provided");
		Mockito.when(accountRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(account));
		AccountDto accountDto = accountService.getAccountDetailsById("1");
		assertEquals(account.getId(), accountDto.getId());
		assertEquals(1, accountDto.getUsers().size());
		account.getUserAccount().forEach(u -> {
			u.setStatus(false);
		});
		AccountDto accountDto1 = accountService.getAccountDetailsById("1");
		assertEquals(1, accountDto.getUsers().size());
		assertEquals(0, accountDto1.getUsers().size());
	}

	@Test
	void testGetActiveAccountsByUser() {
		UserDto userDto = new UserDto();
		ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(userDto, HttpStatus.OK);
		Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.any())).thenReturn(null);
		assertThrows(UserServiceException.class, () -> {
			accountService.getActiveAccountsByUser("1");
		}, "Could not find the requested user");
		assertThrows(UserServiceException.class, () -> {
			accountService.getActiveAccountsByUser(null);
		}, "Could not find the requested user");
		Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.any())).thenReturn(responseEntity);
		Mockito.when(accountRepository.findByUserIdAndStatusTrueAndUserAccountStatusTrue(Mockito.any()))
				.thenReturn(new HashSet<>());
		assertThrows(NoSuchElementException.class, () -> {
			accountService.getActiveAccountsByUser("1");
		}, "No accounts available for user 1");
		Mockito.when(accountRepository.findByUserIdAndStatusTrueAndUserAccountStatusTrue(Mockito.any()))
				.thenReturn(accounts);
		List<AccountDto> result = accountService.getActiveAccountsByUser("1");
		assertEquals(2, result.size());
		for (AccountDto account : result) {
			if (account.getId().equals(1L)) {
				assertTrue(account.isStatus());
			}
			if (account.getId().equals(2L)) {
				assertFalse(account.isStatus());
			}
		}
	}

//	@Test
//	void testGetAllAccountsByUser() {
//		Mockito.when(accountRepository.findByUser(Mockito.any()))
//				.thenReturn(Stream.of(account).collect(Collectors.toSet()));
//		List<AccountDto> result = accountService.getAllAccountsByUser(String.valueOf(user.getId()));
//		assertEquals(2, result.size());
//	}

//	@Test
//	public void testDeactivateAccount() {
//		Optional<Accounts> acc = Optional.of(account);
//		Mockito.when(accountRepository.findById(account.getId())).thenReturn(acc);
//		Mockito.when(accountRepository.save(account)).thenReturn(account);
//		AccountDto response = accountService.updateAccountStatus(String.valueOf(acc.get().getId()));
//		assertFalse(response.isStatus());
//	}
//	
//	@Test
//	public void testAddAccount() {
//		Mockito.when(accountRepository.save(Mockito.any())).thenReturn(account);
//		AccountDto accountDto = new AccountDto(null, "123432", "Domestic", true, Stream.of(Long.valueOf(1)).collect(Collectors.toList()));
//		AccountDto expected = accountService.addAccount(accountDto);
//		assertEquals(1L, expected.getId());
//		assertNull(expected.getUserId());
//	}
}
