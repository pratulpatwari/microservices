package dev.pratul.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ClientPositionSummary {
	private long clientId;
	private List<PositionSummary> positionSummary = new ArrayList<>();
}
