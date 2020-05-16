package dev.pratul.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "assets", schema = "public")
@Getter
@Setter
public class Assets {

	@Id
	@Column(name = "id")
	private Long id;
	
	@Column(name = "symbol")
	private String symbol;
	
	@Column(name = "market_value")
	private double marketValue;
}
