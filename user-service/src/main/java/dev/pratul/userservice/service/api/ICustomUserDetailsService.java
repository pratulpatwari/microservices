package dev.pratul.userservice.service.api;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import dev.pratul.userservice.dto.UserDto;
import dev.pratul.userservice.entity.User;

public interface ICustomUserDetailsService extends UserDetailsService {

	User registerUser(User user);
	
	User getUserById(String userId);
	
	List<UserDto> getUsersByIds(List<Long> ids);
}
