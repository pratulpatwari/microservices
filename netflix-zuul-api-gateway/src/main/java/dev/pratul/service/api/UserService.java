package dev.pratul.service.api;

import java.util.Set;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import dev.pratul.model.User;

@FeignClient("user-service")
@RequestMapping("/api")
public interface UserService {

	/*
	 * Fetch the user by Id
	 */
	@GetMapping("/{id}")
	User getUserById(@PathVariable(value = "id") String id);

	/*
	 * fetch all the users belonging to an organization
	 */
	@GetMapping("/all")
	Set<User> getAllUsers();
}
