package dev.pratul.service.api;

import java.util.List;

import dev.pratul.entity.ClientPosition;

public interface ClientPositionService {

	/*
	 * Fetch all the client positions. Only the active accounts will be pulled in
	 * account-service
	 */
	public List<ClientPosition> getClientPositions(String clientId);

	/*
	 * Fetch client positions for a particular account
	 */
	public List<ClientPosition> getClientPositionsByAccount(String clientId, String accountId);
}
