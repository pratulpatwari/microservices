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
	 * Fetch the account and all users who have access to that account
	 */
	public AccountDto getAccountDetailsById(String accountId);

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
	 * Activate/Deactivate the account. Only users with certain roles can perform
	 * this action. De-activating the account will de-activate at user level as
	 * well. However, re-activating the account will not provide the access to
	 * previously assigned users. If you want to activate the account for each user,
	 * refer updateUserAccount
	 */
	public List<AccountDto> deactivateAccount(String account);

	/*
	 * Activate/Deactivate the account in DB for the mentioned user. Only user with
	 * certain roles can perform this action. If the user passed in as an object is
	 * not already mapped to the account, a new entry will be created with the
	 * mapping, else the status of existing mapping will be changed accordingly
	 */
	public List<AccountDto> updateUserAccount(List<AccountDto> accounts);

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
	
	public void addAccounts();

}
