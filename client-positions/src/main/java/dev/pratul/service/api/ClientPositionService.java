package dev.pratul.service.api;

import java.util.List;

import dev.pratul.entity.Position;

public interface ClientPositionService {

	/*
	 * Fetch all the client positions. Only the active accounts will be pulled in
	 * account-service and the last 7 days of data for those accounts will be
	 * fetched from the DB
	 * 
	 * input: clientId - primary key from user table output: list of client
	 * positions for active accounts for the mentioned user
	 */
	public List<Position> getClientPositions(String clientId);

	/*
	 * Fetch client positions for a particular account
	 * 
	 * input: clientId, accountId output: list of client positions for the mentioned
	 * account. accountId is the primary key from account table
	 */
	public List<Position> getClientPositionsByAccount(String clientId, String accountId);
}
