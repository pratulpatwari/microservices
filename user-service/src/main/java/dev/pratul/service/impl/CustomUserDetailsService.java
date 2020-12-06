package dev.pratul.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.pratul.UserServiceException;
import dev.pratul.dao.RolesRepository;
import dev.pratul.dao.UserRepository;
import dev.pratul.dto.UserDto;
import dev.pratul.entity.Roles;
import dev.pratul.entity.User;
import dev.pratul.service.api.ICustomUserDetailsService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomUserDetailsService implements ICustomUserDetailsService {

	private UserRepository userRepository;
	private RolesRepository roleRepository;

	public CustomUserDetailsService(UserRepository userRepository, RolesRepository roleRepository) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
	}

	@Transactional
	public UserDto getUserById(Long userId) {
		log.debug("Entering getUserById with userId: {}", userId);
		if (userId == null) {
			throw new IllegalArgumentException("No user provided.");
		}
		User user = userRepository.findById(userId).orElseThrow(() -> new NullPointerException("User does not exists"));
		UserDto userDto = new UserDto(user.getId(), user.getFirstName(), user.getMiddleInitial(), user.getStatus(),
				user.getLastName(), user.getEmail(), null);
		Set<Roles> userRoles = user.getRoles();
		UserDto.RoleDto[] rolesDto = userRoles.stream().map(r -> new UserDto.RoleDto(r.getId(), r.getDescription()))
				.toArray(UserDto.RoleDto[]::new);
		userDto.setRoles(rolesDto);
		log.debug("Leaving getUserById with userId: {}", userId);
		return userDto;
	}

	@Transactional
	public List<UserDto> getUsersByIds(List<Long> ids) {
		log.debug("Entering getUsersByIds() with # of users {}", ids != null ? ids.size() : null);
		if (ids == null || ids.isEmpty()) {
			throw new IllegalArgumentException("No users provided to be searched");
		}
		List<User> userList = userRepository.findAllById(ids);
		if (!userList.isEmpty()) {
			List<UserDto> userDtos = new LinkedList<>();
			for (User user : userList) {
				UserDto userDto = new UserDto(user.getId(), user.getFirstName(), user.getMiddleInitial(),
						user.getStatus(), user.getLastName(), user.getEmail(), null);
				Set<Roles> userRoles = user.getRoles();
				UserDto.RoleDto[] rolesDto = userRoles.stream()
						.map(r -> new UserDto.RoleDto(r.getId(), r.getDescription())).toArray(UserDto.RoleDto[]::new);
				userDto.setRoles(rolesDto);
				userDtos.add(userDto);
			}
			log.debug("Leaving getUsersByIds() with # of users {}", userDtos.size());
			return userDtos;
		} else {
			log.error("User list was empty for the given list of user ids: {}", ids);
			throw new UserServiceException("No users found !");
		}
	}

	@Transactional
	public UserDto registerUser(UserDto userDto) {
		log.debug("Entering registerUser with userDto: {}", userDto);
		if (userDto.getRoles() != null && userDto.getRoles().length > 0) {
			Long[] roleIds = new Long[userDto.getRoles().length];
			for (int i = 0; i < roleIds.length; i++) {
				roleIds[i] = userDto.getRoles()[i].getId();
			}
			Roles[] roles = roleRepository.findByIdIn(roleIds);
			if (roles == null) {
				throw new IllegalArgumentException("Invalid roles");
			}
			User user = new User(userDto.getFirstName(), userDto.getMiddleInitial(), userDto.getLastName(),
					userDto.getEmail(), roles);
			try {
				user = userRepository.save(user);
			} catch (IllegalArgumentException ex) {
				log.error("Exception while saving the user object: {}", ex.getMessage());
				throw new IllegalArgumentException("Invalid request. Please contact support team");
			}
			if (user.getId() != null) {
				UserDto newUser = new UserDto(user.getId(), user.getFirstName(), user.getMiddleInitial(),
						user.getStatus(), user.getLastName(), user.getEmail(), null);
				Set<Roles> userRoles = user.getRoles();
				UserDto.RoleDto[] rolesDto = userRoles.stream()
						.map(r -> new UserDto.RoleDto(r.getId(), r.getDescription())).toArray(UserDto.RoleDto[]::new);
				newUser.setRoles(rolesDto);
				log.debug("Leaving registerUser with userDto: {}", userDto.toString());
				return newUser;
			}
		} else {
			throw new IllegalArgumentException("Missing role. User must belong to a role");
		}
		return null;
	}

	@Transactional
	public UserDto updateUserDetails(UserDto userDto) {
		log.debug("Entering updateUserDetails() with userDto: {}", userDto);
		if (userDto.getId() == null) {
			throw new IllegalArgumentException("Could not identify the user");
		} else {
			User user = userRepository.findById(userDto.getId())
					.orElseThrow(() -> new NoSuchElementException("User not found"));
			user.setFirstName(userDto.getFirstName());
			user.setLastName(userDto.getLastName());
			user.setMiddleInitial(userDto.getMiddleInitial());
			user.setStatus(userDto.getStatus());
			if (userDto.getRoles() != null && userDto.getRoles().length > 0) {
				user.getRoles().clear();
				for (int i = 0; i < userDto.getRoles().length; i++) {
					var roleDto = userDto.getRoles()[i];
					Roles role = roleRepository.findById(roleDto.getId())
							.orElseThrow(() -> new IllegalArgumentException("Invalid role"));
					user.getRoles().add(role);
				}
			} else {
				throw new IllegalArgumentException("Roles cannot be blank. Please select atleast one role");
			}
			user = userRepository.save(user);
			UserDto updatedUserDto = new UserDto(user.getId(), user.getFirstName(), user.getMiddleInitial(),
					user.getStatus(), user.getLastName(), user.getEmail(), null);
			UserDto.RoleDto[] rolesDto = user.getRoles().stream()
					.map(r -> new UserDto.RoleDto(r.getId(), r.getDescription())).toArray(UserDto.RoleDto[]::new);
			updatedUserDto.setRoles(rolesDto);
			log.debug("Leaving updateUserDetails() with userDto: {}", userDto);
			return updatedUserDto;
		}
	}
}
