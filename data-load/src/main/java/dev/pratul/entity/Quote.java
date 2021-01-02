package dev.pratul.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
public class Quote {

	private double o;

	private double h;

	private double l;

	private double c;

	private double pc;

	@JsonProperty("openPrice")
	public double getOpenPrice() {
		return this.o;
	}

	@JsonProperty("highPrice")
	public double getHighPrice() {
		return this.h;
	}

	@JsonProperty("lowPrice")
	public double getLowPrice() {
		return this.l;
	}

	@JsonProperty("currentPrice")
	public double getCurrentPrice() {
		return this.c;
	}

	@JsonProperty("lastClosePrice")
	public double getLastClosePrice() {
		return this.pc;
	}
}
