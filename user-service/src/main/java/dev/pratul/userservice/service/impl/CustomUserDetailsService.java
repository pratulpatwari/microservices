package dev.pratul.userservice.service.impl;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.pratul.userservice.dao.UserRepository;
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
	public User getByUserId(String userId) {
		return userRepository.findById(Long.valueOf(userId))
				.orElseThrow(() -> new NoSuchElementException("User " + userId + " does not exists"));
	}

	@Transactional
	public User registerUser(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}
}
