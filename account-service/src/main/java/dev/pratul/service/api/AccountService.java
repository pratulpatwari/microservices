package dev.pratul.service.api;

import java.util.List;

import dev.pratul.dto.AccountDto;

public interface AccountService {

	/**
	 * @param id Primary Key in account table
	 * @return AccountDto
	 * @apiNote Fetch the account by Primary key from account table.
	 */
	public AccountDto getAccountById(long id);

	/*
	 * Fetch the account and all users who have access to that account
	 */
	/**
	 * @param id Primary Key in account table
	 * @return AccountDto
	 * @apiNote Fetch the account by Primary key from account table. Also returns
	 *          the list of users who have access to this account
	 */
	public AccountDto getAccountDetailsById(long id);

	/**
	 * @param userId Primary Key of user whose accounts needs to be returned
	 * @return List of AccountDto
	 * @apiNote Fetch all the accounts for the mentioned user. This is used for the
	 *          admin purpose
	 */
	public List<AccountDto> getAllAccountsByUser(long userId);

	/**
	 * @param account Comma separated list of accounts which needs to be deactivated
	 * @return List of AccountDto which are activated or deactivated
	 * @apiNote Activate/Deactivate the account. Only users with certain roles can
	 *          perform this action. De-activating the account will de-activate at
	 *          user level as well. However, re-activating the account will not
	 *          provide the access to previously assigned users. If you want to
	 *          activate the account for each user, refer updateUserAccount
	 */
	public List<AccountDto> deactivateAccount(String account);

	/**
	 * @param accounts List of accounts with users for whom account needs to be
	 *                 activated or deactivated
	 * @return List of Accounts and user whose status has been changed
	 * @apiNote Activate/Deactivate the account in DB for the mentioned user. Only
	 *          user with certain roles can perform this action. If the user passed
	 *          in as an object is not already mapped to the account, a new entry
	 *          will be created with the mapping, else the status of existing
	 *          mapping will be changed accordingly
	 */
	public List<AccountDto> updateUserAccount(List<AccountDto> accounts);

	/**
	 * @param accountDto with list of userIds to which account needs to be assigned
	 * @return AccountDto of newly created account
	 * 
	 * @apiNote Create an account with given details and assign it to provided list
	 *          of users Step 1: Make a call to users service to get the list of all
	 *          users with id's Step 2: Create account with details and map the
	 *          account to list of users
	 */
	public AccountDto addAccount(AccountDto accountDto);
}
