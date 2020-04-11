package dev.pratul.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/positions")
public class ClientPositionsResources {

	@GetMapping("/val/{clientId}")
	public String getClientPositions(@PathVariable("clientId") String clientId) {
		System.out.println("Client request id: " + clientId);
		return "Hello: " + clientId;
	}
	
	@GetMapping("/lots/{clientId}")
	public String getClientPostionsLots(@PathVariable("clientId") String clientId) {
		System.out.println("Lots for client:  " + clientId);
		return "Lot: " + clientId;
	}
	
}
