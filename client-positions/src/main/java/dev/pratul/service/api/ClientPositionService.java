package dev.pratul.service.api;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import dev.pratul.model.ClientPositionSummary;

public interface ClientPositionService {

	/*
	 * Fetch all the client positions. Only the active accounts will be pulled in
	 * account-service and the last 7 days of data for those accounts will be
	 * fetched from the DB
	 * 
	 * input: clientId - primary key from user table output: list of client
	 * positions for active accounts for the mentioned user
	 */
	public List<ClientPositionSummary> getClientPositions(String clientId, Optional<String> from, Optional<String> to);

	/*
	 * Fetch the client positions using parameters provided
	 */
	public List<ClientPositionSummary> getClientPositions(long userId, Map<String, String> params);
}
