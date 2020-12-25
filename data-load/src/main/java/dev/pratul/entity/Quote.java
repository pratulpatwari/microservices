package dev.pratul.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Quote {

	@JsonProperty("o")
	private double openPrice;

	@JsonProperty("h")
	private double highPrice;

	@JsonProperty("l")
	private double lowPrice;

	@JsonProperty("c")
	private double currentPrice;

	@JsonProperty("pc")
	private double lastClosePrice;
}
