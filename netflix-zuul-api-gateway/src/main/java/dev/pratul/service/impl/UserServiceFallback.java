package dev.pratul.service.impl;

import java.util.Set;

import org.springframework.stereotype.Component;

import dev.pratul.dto.UserDto;
import dev.pratul.service.api.UserService;
import feign.hystrix.FallbackFactory;

@Component
public class UserServiceFallback implements FallbackFactory<UserService> {

	@Override
	public UserService create(Throwable cause) {
		// TODO Auto-generated method stub
		return new UserService() {

			@Override
			public UserDto getUserById(String id) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Set<UserDto> getAllUsers() {
				// TODO Auto-generated method stub
				return null;
			}

		};
	}

}
