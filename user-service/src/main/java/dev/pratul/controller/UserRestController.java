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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.pratul.dto.RoleDto;
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
		return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
	}
	
	@GetMapping("/all")
	public ResponseEntity<List<UserDto>> getAllUsers(){
		return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<List<UserDto>> getUserById(@RequestParam("id") String id) {
		List<UserDto> user = userService.getUsersByUserIds(id);
		return new ResponseEntity<>(user, !user.isEmpty() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
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

	@GetMapping("/roles")
	public ResponseEntity<RoleDto[]> getAllRoles() {
		return new ResponseEntity<>(userService.getAllRoles(), HttpStatus.OK);
	}
}
