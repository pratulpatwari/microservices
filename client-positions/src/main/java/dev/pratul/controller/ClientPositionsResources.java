package dev.pratul.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.pratul.entity.Position;
import dev.pratul.service.api.ClientPositionService;

@RestController
@RequestMapping("/api/position")
public class ClientPositionsResources {

	private ClientPositionService clientPositionService;

	public ClientPositionsResources(ClientPositionService clientPositionService) {
		this.clientPositionService = clientPositionService;
	}

	@GetMapping("/user")
	public ResponseEntity<List<Position>> getClientPositions(@RequestParam("id") String user,
			@RequestParam("from") Optional<String> from, @RequestParam("to") Optional<String> to) {
		return new ResponseEntity<>(clientPositionService.getClientPositions(user), HttpStatus.OK);
	}

	@GetMapping("/account")
	public ResponseEntity<List<Position>> getClientPositionsByAccount(@RequestParam("ids") String account,
			@RequestParam("from") String from, @RequestParam("to") String to) {
//		return new ResponseEntity<>(clientPositionService.getClientPositions(user), HttpStatus.OK);
		return null;
	}
}
