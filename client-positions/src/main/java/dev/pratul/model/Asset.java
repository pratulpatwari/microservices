package dev.pratul.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Asset {
	private long assetId;
	private String assetSymbol;
	private String assetDescription;
	private double assetPrice;
}
