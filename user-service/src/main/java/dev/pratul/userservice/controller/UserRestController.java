package dev.pratul.userservice.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.pratul.userservice.dto.UserDto;
import dev.pratul.userservice.entity.User;
import dev.pratul.userservice.service.api.ICustomUserDetailsService;

@RefreshScope
@RestController
@RequestMapping("/api/user")
public class UserRestController {

	@Autowired
	private ICustomUserDetailsService userService;

	@GetMapping("/{username}")
	public UserDetails getByUsername(@PathVariable("username") String username) {
		return userService.loadUserByUsername(username);
	}

	@GetMapping("/id/{id}")
	public User getUserById(@PathVariable("id") String id) {
		return userService.getUserById(id);
	}

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public User registerUser(@RequestBody @Valid User user) {
		return userService.registerUser(user);
	}

	@PostMapping("/list")
	public ResponseEntity<List<UserDto>> getUsersByIds(@RequestBody @Valid List<Long> userIds) {
		List<UserDto> userDtos = userService.getUsersByIds(userIds);
		if (userDtos != null && userDtos.size() > 0) {
			return new ResponseEntity<>(userDtos, HttpStatus.OK);
		} else
			return new ResponseEntity<>(userDtos, HttpStatus.NOT_FOUND);
	}
}
