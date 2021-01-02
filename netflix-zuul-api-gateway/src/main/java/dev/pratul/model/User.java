package dev.pratul.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

	private Long id;
	private String firstName;
	private String middleInitial;
	private String status;
	private String lastName;
	private String email;
	private Role[] roles;
	
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	static class Role {
		private int id;
		private String name;
	}
}
