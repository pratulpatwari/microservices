package dev.pratul.userservice.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.pratul.userservice.entity.User;
import dev.pratul.userservice.service.api.ICustomUserDetailsService;

@RestController
@RequestMapping("/api")
public class UserRestController {
	
	@Autowired
	private ICustomUserDetailsService userService;

	@GetMapping("/{username}")
	public UserDetails getByUsername(@PathVariable("username") String username) {
		return userService.loadUserByUsername(username);
	}

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public User registerUser(@RequestBody @Valid User user) {
		return userService.registerUser(user);
	}
}
