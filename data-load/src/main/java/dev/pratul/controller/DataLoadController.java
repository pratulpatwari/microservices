package dev.pratul.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.pratul.entity.Quote;
import dev.pratul.service.AssetService;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/data")
public class DataLoadController {
	
	@Autowired
	private AssetService assetService;
	
	@GetMapping
	public Flux<Quote> getData() {
		return assetService.stockPrice();
	}
}
