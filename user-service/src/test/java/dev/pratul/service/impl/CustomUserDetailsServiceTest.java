package dev.pratul.service.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import dev.pratul.UserServiceException;
import dev.pratul.dao.RolesRepository;
import dev.pratul.dao.UserRepository;
import dev.pratul.dto.UserDto;
import dev.pratul.dto.UserDto.RoleDto;
import dev.pratul.entity.Roles;
import dev.pratul.entity.User;

@SpringBootTest
class CustomUserDetailsServiceTest {

	@Autowired
	private CustomUserDetailsService userService;

	@MockBean
	UserRepository userRepository;

	@MockBean
	RolesRepository roleRepository;

	UserDto userDto = new UserDto();

	User user = new User();
	Roles role = new Roles();
	User user1 = new User();
	Roles role1 = new Roles();
	List<User> users = new ArrayList<>();
	Roles[] roles = new Roles[2];

	@BeforeEach
	void createUser() {
		role.setId(1L);
		role.setDescription("Technical");
		user.setId(1L);
		user.setFirstName("Pratul");
		user.setLastName("Patwari");
		user.setEmail("pratul.patwari@gmail.com");
		user.setRoles(Stream.of(role).collect(Collectors.toSet()));
		users.add(user);

		role1.setId(2L);
		role1.setDescription("Business");
		user1.setId(2L);
		user1.setFirstName("John");
		user1.setLastName("Doe");
		user1.setEmail("john.doe@fake.com");
		user1.setRoles(Stream.of(role1).collect(Collectors.toSet()));
		users.add(user1);

		userDto.setFirstName("Pratul");
		userDto.setLastName("Patwari");
		userDto.setEmail("pratul.patwari@gmail.com");
		userDto.setMiddleInitial("K");
		userDto.setStatus("deactive");

		roles[0] = role;
		roles[1] = role1;
	}

	@Test
	void testGetUserById() {
		Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
		UserDto userDto = userService.getUserById(1L);
		assertEquals("Pratul", userDto.getFirstName());
		assertEquals("Patwari", userDto.getLastName());
		assertNotNull(userDto.getRoles());
		assertEquals(1, userDto.getRoles().length);
		assertEquals("Technical", userDto.getRoles()[0].getName());
		Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(null);
		assertThrows(NullPointerException.class, () -> {
			userService.getUserById(1L);
		}, "User not found");
		Mockito.when(userRepository.findById(Mockito.anyLong())).thenThrow(NullPointerException.class);
		assertThrows(NullPointerException.class, () -> {
			userRepository.findById(1L);
		});
		Mockito.when(userRepository.findById(null)).thenReturn(null);
		assertThrows(IllegalArgumentException.class, () -> {
			userService.getUserById(null);
		}, "No user provided.");
	}

	@Test
	void testGetUsersByIds() {
		List<Long> userList = Stream.of(Long.valueOf(1)).collect(Collectors.toList());
		Mockito.when(userRepository.findAllById(null)).thenReturn(null);
		assertThrows(IllegalArgumentException.class, () -> {
			userService.getUsersByIds(null);
		}, "No users provided to be searched");
		Mockito.when(userRepository.findAllById(Mockito.anyIterable())).thenReturn(new ArrayList<>());
		assertThrows(UserServiceException.class, () -> {
			userService.getUsersByIds(userList);
		}, "No users found !");

		Mockito.when(userRepository.findAllById(Mockito.anyIterable()))
				.thenReturn(Stream.of(user).collect(Collectors.toList()));
		List<UserDto> userDtos = userService.getUsersByIds(Stream.of(1L).collect(Collectors.toList()));
		assertEquals(1, userDtos.size());
		assertEquals("Pratul", userDtos.get(0).getFirstName());
		assertEquals(1, userDtos.get(0).getRoles().length);

		Mockito.when(userRepository.findAllById(Mockito.anyIterable()))
				.thenReturn(Stream.of(user1).collect(Collectors.toList()));
		List<Long> ids = new ArrayList<>();
		ids.add(1L);
		ids.add(3L);
		List<UserDto> allUserDtos = userService.getUsersByIds(ids);
		assertEquals(1, allUserDtos.size());
		assertEquals(2L, allUserDtos.get(0).getId());
		assertEquals("John", allUserDtos.get(0).getFirstName());
		assertEquals("Business", allUserDtos.get(0).getRoles()[0].getName());

		Mockito.when(userRepository.findAllById(Mockito.anyIterable())).thenReturn(users);
		List<Long> ids1 = new ArrayList<>();
		ids1.add(Long.valueOf(1));
		ids1.add(Long.valueOf(2));
		List<UserDto> multipleUserDtos = userService.getUsersByIds(ids1);
		assertEquals(2, multipleUserDtos.size());
		assertEquals(1L, multipleUserDtos.get(0).getId());
		assertEquals(2L, multipleUserDtos.get(1).getId());
	}

	@Test
	void testRegisterUser() {
		assertThrows(IllegalArgumentException.class, () -> {
			userService.registerUser(userDto);
		}, "Missing role. User must belong to a role");

		UserDto.RoleDto[] roleDtos = new RoleDto[1];
		roleDtos[0] = new UserDto.RoleDto(1L, "Technical");
		userDto.setRoles(roleDtos);
		Mockito.when(roleRepository.findByIdIn(Mockito.any())).thenReturn(roles);
		Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);
		UserDto result = userService.registerUser(userDto);
		assertEquals(1, result.getRoles().length);
		assertEquals("Technical", result.getRoles()[0].getName());
		assertEquals("Pratul", result.getFirstName());
		assertNotEquals(2L, result.getRoles()[0].getId());
	}

	@Test
	void testUpdateUserDetails() {
		Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
		assertThrows(IllegalArgumentException.class, () -> {
			userService.updateUserDetails(userDto);
		}, "Could not identify the user");
		userDto.setId(1L);
		UserDto.RoleDto[] roleDtos = new RoleDto[1];
		roleDtos[0] = new UserDto.RoleDto(2L, "Business");
		userDto.setRoles(roleDtos);
		Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);
		Mockito.when(roleRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(role1));
		UserDto updatedUserDto = userService.updateUserDetails(userDto);
		assertEquals(1L, updatedUserDto.getId());
		assertEquals("deactive", updatedUserDto.getStatus());
		assertEquals("Business", updatedUserDto.getRoles()[0].getName());
		userDto.setRoles(null);
		assertThrows(IllegalArgumentException.class, () -> {
			userService.updateUserDetails(userDto);
		}, "Roles cannot be blank. Please select atleast one role");
	}

}
