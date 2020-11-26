package dev.pratul.userservice.service.api;

import java.util.List;

import dev.pratul.userservice.dto.UserDto;
import dev.pratul.userservice.entity.User;

public interface ICustomUserDetailsService {

	UserDto registerUser(User user);
	
	UserDto getUserById(Long userId);
	
	List<UserDto> getUsersByIds(List<Long> ids);
}
