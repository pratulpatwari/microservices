package dev.pratul.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Asset {

	private Long id;
	private String symbol;
	private String description;
	private double marketValue;
}
