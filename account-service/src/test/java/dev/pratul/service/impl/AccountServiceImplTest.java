package dev.pratul.service.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

import dev.pratul.dao.AccountRepository;
import dev.pratul.entity.Accounts;
import dev.pratul.entity.Users;
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
	Users user = new Users();

	@BeforeEach
	public void setupRepository() {
		user.setId(1L);

		account.setId(1L);
		account.setStatus(true);
		account.setAccountId("324567");

		deactiveAccount.setId(2L);
		deactiveAccount.setStatus(false);
		deactiveAccount.setAccountId("543232");

		expected.add(account);
		expected.add(deactiveAccount);
	}

	@Test
	public void testGetAccountById() {
		Mockito.when(accountRepository.findByAccountId(String.valueOf(account.getId()))).thenReturn(account);
		Accounts result = accountService.getAccountById(String.valueOf(account.getId()));
		assertEquals(1, result.getId());
	}

	@Test
	public void testGetActiveAccountsByUser() {
		Mockito.when(accountRepository.findByUsersAndStatusTrue(Mockito.any())).thenReturn(expected);
		Set<Accounts> result = accountService.getActiveAccountsByUser(String.valueOf(user.getId()));
		assertEquals(2, result.size());
		for (Accounts account : result) {
			if (account.getId().equals(1L)) {
				assertTrue(account.isStatus());
			}
			if (account.getId().equals(2L)) {
				assertFalse(account.isStatus());
			}

		}
	}

	@Test
	public void testGetAllAccountsByUser() {
		Mockito.when(accountRepository.findByUsers(Mockito.any())).thenReturn(expected);
		Set<Accounts> result = accountService.getAllAccountsByUser(String.valueOf(user.getId()));
		assertEquals(2, result.size());
	}

	@Test
	public void testDeactivateAccount() {
		Optional<Accounts> acc = Optional.of(account);
		Mockito.when(accountRepository.findById(account.getId())).thenReturn(acc);
		Mockito.when(accountRepository.save(account)).thenReturn(account);
		ResponseEntity<Accounts> response = accountService.deactivateAccount(String.valueOf(acc.get().getId()));
		assertFalse(response.getBody().isStatus());
	}
}
