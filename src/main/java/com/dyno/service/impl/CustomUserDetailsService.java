package com.dyno.service.impl;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.dyno.entity.User;
import com.dyno.repository.UserRepository;


@Service
public class CustomUserDetailsService  implements UserDetailsService{

	private final UserRepository userRepository;
	
	public CustomUserDetailsService(UserRepository userRepository) {
		
		this.userRepository=userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username)
				.orElseThrow(()->
				new UsernameNotFoundException("user not found"));
	
		return org.springframework.security.core.userdetails.User
				.withUsername(user.getUsername())
				.password(user.getHashedPass())
				.authorities(List.of())
				.build();
	
	}

}
