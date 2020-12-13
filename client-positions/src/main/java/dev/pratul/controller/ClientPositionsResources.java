package dev.pratul.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.pratul.entity.ClientPosition;
import dev.pratul.service.api.ClientPositionService;

@RestController
@RequestMapping("/api/")
public class ClientPositionsResources {

	private ClientPositionService clientPositionService;
	
	public ClientPositionsResources(ClientPositionService clientPositionService) {
		this.clientPositionService = clientPositionService;
	}

	@GetMapping("{userId}")
	public List<ClientPosition> getClientPositions(@PathVariable("userId") String userId) {
		return clientPositionService.getClientPositions(userId);
	}
}
