package dev.pratul.service.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.pratul.dao.RolesRepository;
import dev.pratul.dao.UserRepository;
import dev.pratul.dto.RoleDto;
import dev.pratul.dto.UserDto;
import dev.pratul.entity.Role;
import dev.pratul.entity.User;
import dev.pratul.exception.UserServiceException;
import dev.pratul.service.api.ICustomUserDetailsService;
import lombok.NonNull;
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

	@Transactional(readOnly = true)
	public UserDto getUserById(@NonNull Long userId) {
		log.debug("Entering getUserById with userId: {}", userId);
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new NoSuchElementException("User does not exists"));
		UserDto userDto = new UserDto(user.getId(), user.getFirstName(), user.getMiddleInitial(),
				user.getStatus(), user.getLastName(), user.getEmail(),
				user.getRoles().stream().map(role -> new RoleDto(role.getId(), role.getDescription()))
						.collect(Collectors.toList()));
		log.debug("Leaving getUserById with userId: {}", userId);
		return userDto;
	}

	@Transactional(readOnly = true)
	public Page<UserDto> getAllUsers(int page, int size) {
		Page<User> user = userRepository.findAll(PageRequest.of(0, 20));
		return user.map(new Function<User, UserDto>() {

			@Override
			public UserDto apply(User user) {
				return new UserDto(user.getId(), user.getFirstName(), user.getMiddleInitial(),
						user.getStatus(), user.getLastName(), user.getEmail(),
						user.getRoles().stream()
								.map(role -> new RoleDto(role.getId(),
										role.getDescription()))
								.collect(Collectors.toList()));
			}

		});
	}

	@Transactional(readOnly = true)
	public List<UserDto> getUsersByIds(List<Long> ids) {
		log.debug("Entering getUsersByIds() with # of users {}", ids.size());
		if (ids.isEmpty()) {
			throw new IllegalArgumentException("No users provided to be searched");
		}
		return getUsersFromDB(ids);
	}

	@Transactional(readOnly = true)
	public List<UserDto> getUsersByUserIds(@NonNull String ids) {
		log.debug("Entering getUsersByIds() with # of users {}", ids);
		List<Long> idLongList = convertStringToLong(ids);
		return getUsersFromDB(idLongList);
	}

	private List<UserDto> getUsersFromDB(List<Long> ids) {
		List<User> userList = userRepository.findAllById(ids);
		if (!userList.isEmpty()) {
			List<UserDto> userDtos = new LinkedList<>();
			for (User user : userList) {
				UserDto userDto = new UserDto(user.getId(), user.getFirstName(),
						user.getMiddleInitial(), user.getStatus(), user.getLastName(),
						user.getEmail(),
						user.getRoles().stream()
								.map(r -> new RoleDto(r.getId(), r.getDescription()))
								.collect(Collectors.toList()));
				userDtos.add(userDto);
			}
			log.debug("Leaving getUsersByIds() with # of users {}", userDtos.size());
			return userDtos;
		} else {
			log.error("User list was empty for the given list of user ids: {}", ids);
			throw new UserServiceException("No users found !");
		}
	}

	private List<Long> convertStringToLong(String stringValue) {
		List<Long> idLongList = new ArrayList<>();
		String[] idString = stringValue.split(",");
		for (int i = 0; i < idString.length; i++) {
			try {
				idLongList.add(Long.valueOf(idString[i].trim()));
			} catch (NumberFormatException ex) {
				log.error("Exception while parsing the user id: {}. Exception: {}", idString[i],
						ex.getMessage());
				throw new NumberFormatException("Invalid user id " + idString[i]);
			}
		}
		return idLongList;
	}

	@Transactional
	public UserDto registerUser(@NonNull UserDto userDto) {
		log.debug("Entering registerUser with userDto: {}", userDto);
		List<Role> roles = roleRepository
				.findByIdIn(userDto.getRoles().stream().mapToInt(role -> role.getId()).toArray());
		if (roles.isEmpty()) {
			throw new IllegalArgumentException("Invalid roles");
		}
		User user = new User(userDto.getFirstName(), userDto.getMiddleInitial(), userDto.getLastName(),
				userDto.getEmail(), roles.toArray(Role[]::new));
		try {
			user = userRepository.save(user);
		} catch (IllegalArgumentException ex) {
			log.error("Exception while saving the user object: {}", ex.getMessage());
			throw new IllegalArgumentException("Invalid request. Please contact support team");
		}
		UserDto newUser = new UserDto(user.getId(), user.getFirstName(), user.getMiddleInitial(),
				user.getStatus(), user.getLastName(), user.getEmail(),
				user.getRoles().stream().map(r -> new RoleDto(r.getId(), r.getDescription()))
						.collect(Collectors.toList()));
		log.debug("Leaving registerUser with userDto: {}", userDto.toString());
		return newUser;
	}

	@Transactional
	public UserDto updateUserDetails(UserDto userDto) {
		log.debug("Entering updateUserDetails() with userDto: {}", userDto);
		if (userDto.getId() == null) {
			throw new IllegalArgumentException("Could not identify the user");
		}
		User user = userRepository.findById(userDto.getId())
				.orElseThrow(() -> new NoSuchElementException("User not found"));
		user.setFirstName(userDto.getFirstName());
		user.setLastName(userDto.getLastName());
		user.setMiddleInitial(userDto.getMiddleInitial());
		user.setStatus(userDto.getStatus());
		if (!userDto.getRoles().isEmpty()) {
			user.getRoles().clear();
			for (RoleDto roleDto : userDto.getRoles()) {
				Role role = roleRepository.findById(roleDto.getId())
						.orElseThrow(() -> new IllegalArgumentException("Invalid role"));
				user.getRoles().add(role);
			}
		} else {
			throw new IllegalArgumentException("Roles cannot be blank. Please select atleast one role");
		}
		user = userRepository.save(user);
		log.debug("Leaving updateUserDetails() with userDto: {}", userDto);
		return new UserDto(user.getId(), user.getFirstName(), user.getMiddleInitial(), user.getStatus(),
				user.getLastName(), user.getEmail(),
				user.getRoles().stream().map(r -> new RoleDto(r.getId(), r.getDescription()))
						.collect(Collectors.toList()));
	}

	@Transactional(readOnly = true)
	public RoleDto[] getAllRoles() {
		log.debug("Entering getAllRoles()");
		List<Role> roles = roleRepository.findAll();
		RoleDto[] roleDto = new RoleDto[roles.size()];
		for (int i = 0; i < roles.size(); i++) {
			roleDto[i] = new RoleDto(roles.get(i).getId(), roles.get(i).getDescription());
		}
		log.debug("Leaving getAllRoles()");
		return roleDto;
	}
}
