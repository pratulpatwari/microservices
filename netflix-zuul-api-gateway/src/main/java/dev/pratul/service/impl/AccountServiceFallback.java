package dev.pratul.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import dev.pratul.dto.AccountDto;
import dev.pratul.service.api.AccountService;
import feign.hystrix.FallbackFactory;

@Component
public class AccountServiceFallback implements FallbackFactory<AccountService> {

	@Override
	public AccountService create(Throwable cause) {

		return new AccountService() {

			@Override
			public Set<AccountDto> getActiveAccountByUserId(long userId) {
				// logic to send some error or data from cache if implemented
				return new HashSet<>();
			}

			@Override
			public Set<AccountDto> getAllAccountsByUser(String userId) {
				// logic to send some error or data from cache if implemented
				return new HashSet<>();
			}

			@Override
			public ResponseEntity<AccountDto> deactivateAccount(String accountId) {
				// logic to send some error or data from cache if implemented
				return null;
			}

		};
	}
}
