package dev.pratul.userservice.service.api;

import org.springframework.security.core.userdetails.UserDetailsService;

import dev.pratul.userservice.entity.User;

public interface ICustomUserDetailsService extends UserDetailsService {

	User registerUser(User user);
	
	User getByUserId(String userId);
}
