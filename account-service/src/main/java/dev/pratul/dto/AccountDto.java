package dev.pratul.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountDto {

	private Long id;
	private String accountId;
	private String accountName;
	private boolean status;
	private List<Long> userId;
}
