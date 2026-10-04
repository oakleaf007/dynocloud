package com.dyno.service.impl;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.dyno.dto.UserResponse;
import com.dyno.entity.Storage;
import com.dyno.entity.User;
import com.dyno.execption.ResourceNotFoundException;
import com.dyno.repository.StorageAccountRepo;
import com.dyno.repository.UserRepository;
import com.dyno.service.UserService;

@Service
public class UserServiceImpl implements UserService{
	private final UserRepository userRepository;
	private final StorageAccountRepo storageRepo;
	
	public UserServiceImpl(UserRepository userRepository, StorageAccountRepo storageRepo) {
	
		this.userRepository = userRepository;
		this.storageRepo = storageRepo;
	}


	@Override
	public UserResponse getCurrentUser(String username) {
		
		User user = userRepository.findByUsername(username)
					.orElseThrow(()->new RuntimeException("User not found"));
	
		Storage storage = storageRepo.findByUserId(user.getId())
					.orElseThrow(()->
							new ResourceNotFoundException("User quota not found")
							);
		return new UserResponse(
					user.getUsername(),
					user.getEmail(),
					storage.getQuotaBytes(),
					storage.getUsedBytes()
				);
	}


	@Override
	public User getUserByUsername(String username) {
		return userRepository.findByUsername(username)
				.orElseThrow(()->
					new UsernameNotFoundException("user not found"));
					
				}
	

}
