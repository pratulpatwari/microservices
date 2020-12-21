package dev.pratul.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "account", schema = "public")
@Getter
@Setter
@ToString
public class Account {
	
	@Id
	@Column(name = "id")
	private Long id;

	@Column(name = "acc_id")
	private String accountId;

	@Column(name = "acc_name")
	private String accountName;

}
