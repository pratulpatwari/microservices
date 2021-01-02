package dev.pratul.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.pratul.entity.Asset;
import dev.pratul.entity.Quote;
import dev.pratul.entity.Stock;
import dev.pratul.service.DataloadService;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/data")
public class DataLoadController {

	@Autowired
	private DataloadService dataloadService;

	@GetMapping
	public Flux<Quote> getData(@RequestParam("stock") String stock) {
		return dataloadService.stockPrice(stock);
	}

	@GetMapping("/stocks")
	public Flux<Stock> getData() {
		return dataloadService.supportedStock();
	}
	
	@GetMapping("saved")
	public List<Asset> getSavedAssets(){
		return dataloadService.loadAssetsFromDB();
	}
}
