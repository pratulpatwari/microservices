package dev.pratul.service.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import dev.pratul.dao.RolesRepository;
import dev.pratul.dao.UserRepository;
import dev.pratul.dto.RoleDto;
import dev.pratul.dto.UserDto;
import dev.pratul.entity.Role;
import dev.pratul.entity.User;
import dev.pratul.exception.UserServiceException;

@SpringBootTest
class UserDetailsServiceImplTest {

	@Autowired
	private UserDetailsServiceImpl userService;

	@MockBean
	UserRepository userRepository;

	@MockBean
	RolesRepository roleRepository;

	UserDto userDto = new UserDto();

	User user = new User();
	Role role = new Role();
	User user1 = new User();
	Role role1 = new Role();
	List<User> users = new ArrayList<>();
	Role[] roles = new Role[2];

	@BeforeEach
	void createUser() {
		role.setId(1);
		role.setDescription("Technical");
		user.setId(1L);
		user.setFirstName("Pratul");
		user.setLastName("Patwari");
		user.setEmail("pratul.patwari@gmail.com");
		user.setRoles(Stream.of(role).collect(Collectors.toSet()));
		users.add(user);

		role1.setId(2);
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
		assertEquals(1, userDto.getRoles().size());
		assertEquals("Technical", userDto.getRoles().get(0).getName());
		Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(null);
		assertThrows(NullPointerException.class, () -> {
			userService.getUserById(1L);
		}, "User not found");
		Mockito.when(userRepository.findById(Mockito.anyLong())).thenThrow(NullPointerException.class);
		assertThrows(NullPointerException.class, () -> {
			userRepository.findById(1L);
		});
		Mockito.when(userRepository.findById(null)).thenReturn(null);
		assertThrows(NullPointerException.class, () -> {
			userService.getUserById(null);
		}, "No user provided.");
	}

	@Test
	void testGetUsersByIds() {
		List<Long> userList = Stream.of(Long.valueOf(1)).collect(Collectors.toList());
		Mockito.when(userRepository.findAllById(null)).thenReturn(null);
		assertThrows(NullPointerException.class, () -> {
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
		assertEquals(1, userDtos.get(0).getRoles().size());

		Mockito.when(userRepository.findAllById(Mockito.anyIterable()))
				.thenReturn(Stream.of(user1).collect(Collectors.toList()));
		List<Long> ids = new ArrayList<>();
		ids.add(1L);
		ids.add(3L);
		List<UserDto> allUserDtos = userService.getUsersByIds(ids);
		assertEquals(1, allUserDtos.size());
		assertEquals(2L, allUserDtos.get(0).getId());
		assertEquals("John", allUserDtos.get(0).getFirstName());
		assertEquals("Business", allUserDtos.get(0).getRoles().get(0).getName());

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
	void testGetUsersByUserIds() {
		Mockito.when(userRepository.findAllById(null)).thenReturn(null);
		assertThrows(NullPointerException.class, () -> {
			userService.getUsersByUserIds(null);
		}, "No users provided to be searched");
		Mockito.when(userRepository.findAllById(Mockito.anyIterable())).thenReturn(new ArrayList<>());
		assertThrows(UserServiceException.class, () -> {
			userService.getUsersByUserIds("1,2,3,4");
		}, "No users found !");

		Mockito.when(userRepository.findAllById(Mockito.anyIterable()))
				.thenReturn(Stream.of(user).collect(Collectors.toList()));
		List<UserDto> userDtos = userService.getUsersByUserIds("1");
		assertEquals(1, userDtos.size());
		assertEquals("Pratul", userDtos.get(0).getFirstName());
		assertEquals(1, userDtos.get(0).getRoles().size());

		Mockito.when(userRepository.findAllById(Mockito.anyIterable()))
				.thenReturn(Stream.of(user1).collect(Collectors.toList()));
		List<UserDto> allUserDtos = userService.getUsersByUserIds("1,3");
		assertEquals(1, allUserDtos.size());
		assertEquals(2L, allUserDtos.get(0).getId());
		assertEquals("John", allUserDtos.get(0).getFirstName());
		assertEquals("Business", allUserDtos.get(0).getRoles().get(0).getName());

		Mockito.when(userRepository.findAllById(Mockito.anyIterable())).thenReturn(users);
		List<UserDto> multipleUserDtos = userService.getUsersByUserIds("1,2");
		assertEquals(2, multipleUserDtos.size());
		assertEquals(1L, multipleUserDtos.get(0).getId());
		assertEquals(2L, multipleUserDtos.get(1).getId());

		assertThrows(NumberFormatException.class, () -> {
			userService.getUsersByUserIds("1abc");
		});
	}

	@Test
	void testRegisterUser() {
		assertThrows(IllegalArgumentException.class, () -> {
			userService.registerUser(userDto);
		}, "Missing role. User must belong to a role");
		List<Role> roles = List.of(new Role(1, "Technical", "Technical members of the team"));
		userDto.setRoles(List.of(new RoleDto(1, "Technical")));
		Mockito.when(roleRepository.findByIdIn(Mockito.any())).thenReturn(roles);
		Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);
		UserDto result = userService.registerUser(userDto);
		assertEquals(1, result.getRoles().size());
		assertEquals("Technical", result.getRoles().get(0).getName());
		assertEquals("Pratul", result.getFirstName());
		assertNotEquals(2L, result.getRoles().get(0).getId());
	}

	@Test
	void testUpdateUserDetails() {
		Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
		assertThrows(NoSuchElementException.class, () -> {
			userService.updateUserDetails(userDto);
		}, "Could not identify the user");
		userDto.setId(1L);
		userDto.setRoles(List.of(new RoleDto(2, "Business")));
		Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);
		Mockito.when(roleRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(role1));
		UserDto updatedUserDto = userService.updateUserDetails(userDto);
		assertEquals(1L, updatedUserDto.getId());
		assertEquals("deactive", updatedUserDto.getStatus());
		assertEquals("Business", updatedUserDto.getRoles().get(0).getName());
		userDto.setRoles(null);
		assertThrows(NullPointerException.class, () -> {
			userService.updateUserDetails(userDto);
		}, "Roles cannot be blank. Please select atleast one role");
	}

	@Test
	void testGetAllRoles() {
		List<Role> roles = new ArrayList<>();
		roles.add(new Role(1, "TECH","Technicial"));
		roles.add(new Role(2, "BUS","Business"));
		roles.add(new Role(3, "ADMIN","Admin"));
		Mockito.when(roleRepository.findAll()).thenReturn(roles);
		RoleDto[] roleDto = userService.getAllRoles();
		assertEquals(3, roleDto.length);
		assertNotEquals("Technical", roleDto[1].getName());
		assertEquals("Admin", roleDto[2].getName());
		assertEquals(1L, roleDto[0].getId());
	}

}
