package com.dyno.service.impl;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.dyno.dto.UserResponse;
import com.dyno.entity.User;
import com.dyno.repository.UserRepository;
import com.dyno.service.UserService;

@Service
public class UserServiceImpl implements UserService{
	private final UserRepository userRepository;
	
	public UserServiceImpl(UserRepository userRepository) {
	
		this.userRepository = userRepository;
	}


	@Override
	public UserResponse getCurrentUser(String username) {
		
		User user = userRepository.findByUsername(username)
					.orElseThrow(()->new RuntimeException("User not found"));
	
		
		return new UserResponse(
					user.getUsername(),
					user.getEmail()
				);
	}


	@Override
	public User getUserByUsername(String username) {
		return userRepository.findByUsername(username)
				.orElseThrow(()->
					new UsernameNotFoundException("user not found"));
					
				}
	

}
