package dev.pratul.userservice.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.pratul.userservice.dao.UserRepository;
import dev.pratul.userservice.dto.UserDto;
import dev.pratul.userservice.entity.User;
import dev.pratul.userservice.service.api.ICustomUserDetailsService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomUserDetailsService implements ICustomUserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.debug("User to be authenticated: ", username);
		Optional<User> user = userRepository.findByUserName(username);
		user.orElseThrow(() -> new NoSuchElementException("Username " + username + " not found !"));
		return new CustomUserDetails(user.get());
	}

	@Transactional
	public User getUserById(String userId) {
		return userRepository.findById(Long.valueOf(userId))
				.orElseThrow(() -> new NoSuchElementException("User " + userId + " does not exists"));
	}

	@Transactional
	public User registerUser(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

	@Transactional
	public List<UserDto> getUsersByIds(List<Long> ids) {
		log.debug("Entering getUsersByIds() with # of users {}", ids.size());
		List<User> userList = userRepository.findAllById(ids);
		List<UserDto> userDtos = new LinkedList<>();
		for (User user : userList) {
			userDtos.add(new UserDto(user.getId(), user.getFirstName(), user.getMiddleInitial(), user.getStatus(),
					user.getLastName(), user.getEmail()));
		}
		log.debug("Leaving getUsersByIds() with # of users {}", userDtos.size());
		return userDtos;
	}
}
