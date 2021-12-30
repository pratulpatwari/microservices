package dev.pratul.service.api;

import java.util.Map;

import dev.pratul.model.ClientPositionSummary;
import reactor.core.publisher.Mono;

public interface ClientPositionService {

	
	public Mono<ClientPositionSummary> getClientPositions(long userId, Map<String, String> params);
}
