package dev.pratul.service.impl;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;

import dev.pratul.dao.AccountRepository;
import dev.pratul.dto.AccountDto;
import dev.pratul.dto.UserDto;
import dev.pratul.entity.Account;
import dev.pratul.entity.User;
import dev.pratul.entity.UserAccount;
import dev.pratul.model.AccountMapper;
import dev.pratul.service.api.AccountService;

@SpringBootTest
class AccountServiceImplTest {

	@Autowired
	private AccountService accountService;

	@MockBean
	private AccountRepository accountRepository;

	@MockBean
	private RestTemplate restTemplate;

	@MockBean
	private JdbcTemplate jdbcTemplate;

	Set<Account> accounts = new HashSet<>();
	Account account = new Account();
	Account deactiveAccount = new Account();
	User user = new User();
	UserAccount userAccount = new UserAccount();

	@BeforeEach
	public void setupRepository() {
		user.setId(1L);

		account.setId(1L);
		account.setStatus(true);
		account.setAccountId("324567");
		account.setUser(Stream.of(user).collect(Collectors.toSet()));
		userAccount.setAccount(account);
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
		Mockito.when(accountRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(account));
		AccountDto result = accountService.getAccountById(account.getId());
		assertEquals(1, result.getId());
		assertTrue(result.isStatus());
		assertEquals(0, result.getUsers().size());
		Mockito.when(accountRepository.findById(Mockito.anyLong())).thenThrow(NoSuchElementException.class);
		assertThrows(NoSuchElementException.class, () -> {
			accountService.getAccountById(2L);
		});
	}

	@Test
	void testAccountDetailsById() {
		AccountDto accountDto = new AccountDto();
		accountDto.setAccountId("1234");
		accountDto.setId(1L);
		accountDto.setStatus(true);
		accountDto.setAccountName("Domestic Account");
		Mockito.when(jdbcTemplate.queryForObject(Mockito.any(), Mockito.any(AccountMapper.class), Mockito.any()))
				.thenReturn(accountDto);
		AccountDto result = accountService.getAccountDetailsById(1L);
		assertEquals(0, result.getUsers().size());
		assertEquals("Domestic Account", result.getAccountName());
		assertEquals(1L, result.getId());
		assertEquals("1234", result.getAccountId());
		Mockito.when(jdbcTemplate.queryForObject(Mockito.any(), Mockito.any(AccountMapper.class), Mockito.any()))
				.thenReturn(null);
		assertThrows(NoSuchElementException.class, () -> {
			accountService.getAccountDetailsById(1L);
		});
	}

	@Test
	void testGetAllAccountsByUser() {
		AccountDto accountDtoActive = new AccountDto(1L, "fj23kb", "Global Account", true);
		UserDto userDto = new UserDto(1L, "Pratul", "K", "Active", "Patwari", "pratul.patwari@gmail.com", null);
		accountDtoActive.getUsers().add(userDto);
		List<AccountDto> accounts = new ArrayList<>();
		accounts.add(accountDtoActive);
		Mockito.when(jdbcTemplate.query(Mockito.any(), Mockito.any(AccountMapper.class), Mockito.any()))
				.thenReturn(accounts);
		List<AccountDto> result = accountService.getAllAccountsByUser(1);
		assertEquals(1, result.size());
	}
}
