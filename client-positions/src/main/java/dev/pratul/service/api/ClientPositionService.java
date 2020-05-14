package dev.pratul.service.api;

import java.util.List;

import dev.pratul.entity.ClientPosition;

public interface ClientPositionService {

	public List<ClientPosition> getClientPositions(String clientId);

	public List<ClientPosition> getClientPositionsByAccount(String clientId, String accountId);
}
