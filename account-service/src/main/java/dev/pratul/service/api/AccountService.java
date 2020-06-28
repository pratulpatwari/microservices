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

	/*
	 * Create an account with given details and assign it to provided list of users
	 * 
	 * Step 1: Make a call to users service to get the list of all users with id's
	 * Step 2: Create account with details and map the account to list of users
	 * 
	 * @param: AccountDto with list of userIds to which account needs to be assigned
	 * 
	 * @return: AccountDto of newly created account
	 */
	public AccountDto addAccount(AccountDto accountDto);

}
