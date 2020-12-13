package dev.pratul.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.pratul.dto.UserDto;
import dev.pratul.service.api.ICustomUserDetailsService;

@RestController
@RequestMapping("/api/user")
public class UserRestController {

	private ICustomUserDetailsService userService;

	public UserRestController(ICustomUserDetailsService userService) {
		this.userService = userService;
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserDto> getUserById(@PathVariable("id") Long id) {
		UserDto user = userService.getUserById(id);
		return new ResponseEntity<>(user, user != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}

	@PostMapping
	public ResponseEntity<UserDto> registerUser(@RequestBody @Valid UserDto userDto) {
		UserDto newUser = userService.registerUser(userDto);
		return new ResponseEntity<>(newUser, newUser != null ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST);
	}

	@PostMapping("/ids")
	public ResponseEntity<List<UserDto>> getUsersByIds(@RequestBody @Valid List<Long> userIds) {
		List<UserDto> users = userService.getUsersByIds(userIds);
		return new ResponseEntity<>(users, users != null ? HttpStatus.OK : HttpStatus.NO_CONTENT);
	}

	@PutMapping("/modify")
	public ResponseEntity<UserDto> updateUserDetails(@RequestBody @Valid UserDto userDto) {
		UserDto updatedUserDto = userService.updateUserDetails(userDto);
		return new ResponseEntity<>(updatedUserDto, updatedUserDto != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
	}
}
