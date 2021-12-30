package dev.pratul.dto;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.lang.Nullable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

	private Long id;
	private String firstName;
	@Nullable
	@Size(max = 1, message = "Middle Initial can have only one character")
	private String middleInitial;
	private String status;
	private String lastName;
	private String email;
	@NotNull(message = "Roles cannot be blank")
	private List<RoleDto> roles = new ArrayList<>();
}
