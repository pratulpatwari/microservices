package dev.pratul.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/data")
public class DataLoadController {
	
	@GetMapping
	public String getData() {
		return "Hello Pratul";
	}
}
