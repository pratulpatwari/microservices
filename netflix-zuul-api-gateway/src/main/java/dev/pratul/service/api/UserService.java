package dev.pratul.service.api;

import java.util.Set;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import dev.pratul.dto.UserDto;
import feign.Headers;

@Service
@Headers({ "Accept=application/json" })
@FeignClient("user-service")
@RequestMapping("/api")
public interface UserService {

	/*
	 * Fetch the user by Id
	 */
	@GetMapping("/{id}")
	UserDto getUserById(@PathVariable(value = "id") String id);

	/*
	 * fetch all the users belonging to an organization
	 */
	@GetMapping("/all")
	Set<UserDto> getAllUsers();
}
