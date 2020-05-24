package dev.pratul.model;

import lombok.Data;

@Data
public class Account {

	private Long id;
	private String accountId;
	private String accountName;
	private boolean status;
	
}
