package dev.pratul.entity;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "position", schema = "public")
@Getter
@Setter
public class Position {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_gen")
	@SequenceGenerator(name = "seq_gen", sequenceName = "position_id_seq", schema = "public", allocationSize = 1)
	private Long id;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "acc_id")
	private Account accounts;
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "asset_id")
	private Asset assetId;
	
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
