package dev.pratul.entity;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "positions", schema = "public")
@Getter
@Setter
public class ClientPosition {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_gen")
	@SequenceGenerator(name = "seq_gen", sequenceName = "positions_id_seq", schema = "public", allocationSize = 1)
	private Long id;
	
	@Column(name = "account_id")
	private Long accountId;
	
	@Column(name = "asset_id")
	private Long assetId;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "market_value")
	private double marketValue;
	
	@Column(name = "create_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
	private ZonedDateTime createDate;

	@Column(name = "update_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
	private ZonedDateTime updateDate;

}
