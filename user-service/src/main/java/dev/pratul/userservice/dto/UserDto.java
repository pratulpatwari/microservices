package dev.pratul.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

	private Long id;
	private String firstName;
	private String middleInitial;
	private String status;
	private String lastName;
	private String email;
}
