package dev.pratul.userservice.entity;

import lombok.Getter;

@Getter
public enum RoleType {
	ROLE_TECH("TECH"),
	ROLE_BUSINESS("BUSINESS");
	
	
	private final String role;

    private RoleType(String role) {
        this.role = role;
    }
}
