package dev.pratul.userservice.service.impl;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.pratul.UserServiceException;
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

	@Transactional
	public UserDto getUserById(String userId) {
		User user = userRepository.findById(Long.valueOf(userId))
				.orElseThrow(() -> new UserServiceException("User " + userId + " does not exists"));
		return new UserDto(user.getId(), user.getFirstName(), user.getMiddleInitial(), user.getStatus(),
				user.getLastName(), user.getEmail());

	}

	@Transactional
	public List<UserDto> getUsersByIds(List<Long> ids) {
		log.debug("Entering getUsersByIds() with # of users {}", ids.size());
		List<User> userList = userRepository.findAllById(ids);
		if (userList != null && userList.size() > 0) {
			List<UserDto> userDtos = new LinkedList<>();
			for (User user : userList) {
				userDtos.add(new UserDto(user.getId(), user.getFirstName(), user.getMiddleInitial(), user.getStatus(),
						user.getLastName(), user.getEmail()));
			}
			log.debug("Leaving getUsersByIds() with # of users {}", userDtos.size());
			return userDtos;
		} else {
			throw new UserServiceException("No users found !");
		}

	}

	@Transactional
	public UserDto registerUser(User user) {
		// TODO Auto-generated method stub
		return null;
	}
}
