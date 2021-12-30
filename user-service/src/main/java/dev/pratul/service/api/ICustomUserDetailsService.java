package dev.pratul.service.api;

import java.util.List;

import org.springframework.data.domain.Page;

import dev.pratul.dto.RoleDto;
import dev.pratul.dto.UserDto;

public interface ICustomUserDetailsService {

	/**
	 * @return RoleDto array
	 * 
	 * @apiNote Return the list of all available roles
	 */
	RoleDto[] getAllRoles();

	/**
	 * @return List of UserDto
	 * 
	 * @apiNote Return the list of all users in the DB
	 */
	Page<UserDto> getAllUsers(int page, int size);

	/**
	 * @param userDto - user which needs to be onboarded
	 * 
	 * @return UserDto with userId
	 * 
	 * @apiNote Register a new user using the DTO object. Status is by default
	 *          marked as active which means any user created will be default active
	 * 
	 * @throws IllegalArgumentException
	 */
	UserDto registerUser(UserDto userDto);

	/**
	 * @param userId
	 * 
	 * @return UserDto
	 * 
	 * @apiNote Return the user using it's unique identifier
	 * 
	 * @throws IllegalArgumentException
	 */
	UserDto getUserById(Long userId);

	/**
	 * @param ids comma separated
	 * 
	 * @return List of all users requested
	 * 
	 * @apiNote Return the list of all users requested using query parameter. If
	 *          multiple users are to be requested, id needs to be sent with comma
	 *          separated values
	 * 
	 * @throws IllegalArgumentException
	 */
	List<UserDto> getUsersByUserIds(String ids);

	/**
	 * @param ids List of user ids
	 * 
	 * @return List of UserDto
	 * 
	 * @apiNote Return the list of requested users
	 * 
	 * @throws IllegalArgumentException
	 */
	List<UserDto> getUsersByIds(List<Long> ids);

	/**
	 * @param userDto
	 * 
	 * @return UserDto with updated details
	 * 
	 * @apiNote This method expects every field to be present in request DTO object.
	 *          If the field needs to be same as previous value, then same value
	 *          needs to be set in the DTO
	 * 
	 * @throws IllegalArgumentException
	 */
	UserDto updateUserDetails(UserDto userDto);
}
