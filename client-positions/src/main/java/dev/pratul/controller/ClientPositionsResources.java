package dev.pratul.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.pratul.entity.ClientPosition;
import dev.pratul.service.api.ClientPositionService;

@RestController
@RequestMapping("/api/")
public class ClientPositionsResources {

	@Autowired
	private ClientPositionService clientPositionService;

	@GetMapping("{userId}")
	public List<ClientPosition> getClientPositions(@PathVariable("userId") String userId) {
		List<ClientPosition> cientPositions = clientPositionService.getClientPositions(userId);
		return cientPositions;
	}

	@GetMapping("lots/{clientId}")
	public String getClientPostionsLots(@PathVariable("clientId") String clientId) {
		System.out.println("Lots for client:  " + clientId);
		return "Lot: " + clientId;
	}

}
