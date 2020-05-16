package dev.pratul.service.api;

import java.util.Set;

import dev.pratul.entity.Accounts;

public interface AccountService {

	/*
	 * Fetch the account by account Id. This is NOT the DB primary key in accounts
	 * table but the actual account number
	 */
	public Accounts getAccountById(String accountId);

	/*
	 * Fetch only the active accounts for the mentioned user
	 */
	public Set<Accounts> getActiveAccountsByUser(String userId);

	/*
	 * Fetch all the accounts for the mentioned user. This is used for the admin
	 * purpose
	 */
	public Set<Accounts> getAllAccountsByUser(String userId);

}