package dev.pratul.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.pratul.model.ClientPositionSummary;
import dev.pratul.service.api.ClientPositionService;

@RestController
@RequestMapping("/api/position")
public class ClientPositionsResources {

	private ClientPositionService clientPositionService;

	public ClientPositionsResources(ClientPositionService clientPositionService) {
		this.clientPositionService = clientPositionService;
	}

	@GetMapping("/{user}")
	public ResponseEntity<List<ClientPositionSummary>> getClientPositions(@PathVariable("user") long user,
			@RequestParam Map<String, String> allParams) {
		return new ResponseEntity<>(clientPositionService.getClientPositions(user, allParams), HttpStatus.OK);
	}
}
