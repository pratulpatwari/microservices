package dev.pratul.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class PositionSummary {

	private long id;
	private Asset asset;
	private Account account;
	private double marketValue;
	private LocalDate valuationDate;
}
