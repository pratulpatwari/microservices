package dev.pratul.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Position {
	private long positionId;
	private String description;
	private double marketValue;
	private LocalDate valuationDate;
	private Asset asset;
	private Account account;
}
