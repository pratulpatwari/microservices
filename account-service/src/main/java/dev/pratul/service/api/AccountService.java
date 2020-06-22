package dev.pratul.service.api;

import java.util.List;

import dev.pratul.dto.AccountDto;

public interface AccountService {

	/*
	 * Fetch the account by account Id. This is NOT the DB primary key in accounts
	 * table but the actual account number
	 */
	public AccountDto getAccountById(String accountId);

	/*
	 * Fetch only the active accounts for the mentioned user
	 */
	public List<AccountDto> getActiveAccountsByUser(String userId);

	/*
	 * Fetch all the accounts for the mentioned user. This is used for the admin
	 * purpose
	 */
	public List<AccountDto> getAllAccountsByUser(String userId);

	/*
	 * Deactivate the account in DB. Only user with certain roles can perform this
	 * action.
	 */
	public AccountDto deactivateAccount(String accountId);

	/*
	 * Deactivate the account in DB for the mentioned user. Only user with certain
	 * roles can perform this action.
	 */
	public boolean deactivateUserAccount(String userId, String accountId);

}
