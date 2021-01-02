package dev.pratul.entity;

import lombok.Data;

@Data
public class Stock {
	private String currency;
	private String description;
	private String displaySymbol;
	private String figi;
	private String mic;
	private String symbol;
	private String type;
}
