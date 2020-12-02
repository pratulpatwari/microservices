package dev.pratul.service.api;

import java.util.List;

import dev.pratul.dto.UserDto;

public interface ICustomUserDetailsService {

	UserDto registerUser(UserDto userDto);
	
	UserDto getUserById(Long userId);
	
	List<UserDto> getUsersByIds(List<Long> ids);
}
