	package dev.pratul.service.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashSet;
import java.util.List;
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

import dev.pratul.dao.AccountRepository;
import dev.pratul.dto.AccountDto;
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

	Set<Accounts> expected = new HashSet<>();
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

		deactiveAccount.setId(2L);
		deactiveAccount.setStatus(false);
		deactiveAccount.setAccountId("543232");

		expected.add(account);
		expected.add(deactiveAccount);
	}

	@Test
	public void testGetAccountById() {
		Mockito.when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
		AccountDto result = accountService.getAccountById(String.valueOf(account.getId()));
		assertEquals(1, result.getId());
		assertThrows(NullPointerException.class, () -> {
			accountService.getAccountById("2");
		});
	}
//
//	@Test
//	public void testGetActiveAccountsByUser() {
//		Mockito.when(accountRepository.findByUser_IdAndStatusTrueAndUserAccount_StatusTrue(Mockito.any())).thenReturn(expected);
//		List<AccountDto> result = accountService.getActiveAccountsByUser("1");
//		assertEquals(2, result.size());
//		for (AccountDto account : result) {
//			if (account.getId().equals(1L)) {
//				assertTrue(account.isStatus());
//			}
//			if (account.getId().equals(2L)) {
//				assertFalse(account.isStatus());
//			}
//		}
//	}

//	@Test
//	public void testGetAllAccountsByUser() {
//		Mockito.when(accountRepository.findByUsers(Mockito.any())).thenReturn(expected);
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
