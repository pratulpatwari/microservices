package dev.pratul.model;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import dev.pratul.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Account {
	
	private Long id;
	@NotBlank(message = "Account Id cannot be blank")
	@NotNull
	private String accountId;
	private String accountName;
	private boolean status;
	private List<UserDto> users = new ArrayList<>();
}
