package dev.pratul.service.api;

import java.util.List;

import dev.pratul.dto.UserDto;

public interface ICustomUserDetailsService {

	/*
	 * Register a new user using the DTO object. Status is by default marked as
	 * active which means any user created will be default be active
	 */
	UserDto registerUser(UserDto userDto);

	/*
	 * Fetch the user using it's unique identifier
	 */
	UserDto getUserById(Long userId);

	/*
	 * Fetch the list of all users requested using query parameter. If multiple
	 * users are to be requested, id needs to be sent with comma separated values
	 */
	List<UserDto> getUsersByUserIds(String ids);

	/*
	 * Fetch the list of all users using their Ids
	 */
	List<UserDto> getUsersByIds(List<Long> ids);

	/*
	 * This method expects every field to be present in request DTO object. If the
	 * field needs to be same as previous value, then same value needs to be set in
	 * the DTO
	 */
	UserDto updateUserDetails(UserDto userDto);
}
