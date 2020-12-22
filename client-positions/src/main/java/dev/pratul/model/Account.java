package dev.pratul.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Account {
	
	private Long id;
	private String accountId;
	private String accountName;
	private boolean status;
}
