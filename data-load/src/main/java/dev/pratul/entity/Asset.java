package dev.pratul.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Asset {

	private Long id;
	private String symbol;
	private String description;

	@JsonProperty(value = "market_value")
	private double marketValue;
}
