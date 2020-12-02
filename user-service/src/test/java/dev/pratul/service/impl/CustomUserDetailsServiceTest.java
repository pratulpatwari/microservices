package dev.pratul.service.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import dev.pratul.dao.UserRepository;
import dev.pratul.dto.UserDto;
import dev.pratul.entity.Roles;
import dev.pratul.entity.User;

@SpringBootTest
class CustomUserDetailsServiceTest {

	@Autowired
	private CustomUserDetailsService userService;

	@MockBean
	UserRepository userRepository;

	User user = new User();
	Roles role = new Roles();

	@BeforeEach
	void createUser() {
		role.setId(1L);
		role.setDescription("Technical");

		user.setId(1L);
		user.setFirstName("Pratul");
		user.setLastName("Patwari");
		user.setEmail("pratul.patwari@gmail.com");
		user.setRoles(Stream.of(role).collect(Collectors.toSet()));
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
	}

}
