package dev.pratul.userservice.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

	@GetMapping("/id/{id}")
	public ResponseEntity<UserDto> getUserById(@PathVariable("id") String id) {
		UserDto user = userService.getUserById(id);
		return new ResponseEntity<>(user != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}

	@PostMapping("/register")
	public ResponseEntity<UserDto> registerUser(@RequestBody @Valid User user) {
		UserDto newUser = userService.registerUser(user);
		return new ResponseEntity<>(newUser != null ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST);
	}

	@PostMapping("/list")
	public ResponseEntity<List<UserDto>> getUsersByIds(@RequestBody @Valid List<Long> userIds) {
		List<UserDto> users = userService.getUsersByIds(userIds);
		return new ResponseEntity<>(users != null ? HttpStatus.OK : HttpStatus.NO_CONTENT);
	}
}
