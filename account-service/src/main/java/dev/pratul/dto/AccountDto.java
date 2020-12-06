package dev.pratul.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {

	private Long id;
	private String accountId;
	private String accountName;
	private boolean status;
	private List<UserDto> users = new ArrayList<>();

	public AccountDto(Long id, String accountId, String accountName, boolean status) {
		this.id = id;
		this.accountId = accountId;
		this.accountName = accountName;
		this.status = status;
	}
}
