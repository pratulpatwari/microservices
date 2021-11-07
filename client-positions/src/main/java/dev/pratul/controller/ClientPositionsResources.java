package dev.pratul.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.pratul.model.ClientPositionSummary;
import dev.pratul.service.api.ClientPositionService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/position")
public class ClientPositionsResources {

	private ClientPositionService clientPositionService;

	public ClientPositionsResources(ClientPositionService clientPositionService) {
		this.clientPositionService = clientPositionService;
	}

	@GetMapping("/{user}")
	public Mono<ClientPositionSummary> getClientPositions(@PathVariable("user") long user,
			@RequestParam Map<String, String> allParams) {
		return clientPositionService.getClientPositions(user, allParams);
	}
}
