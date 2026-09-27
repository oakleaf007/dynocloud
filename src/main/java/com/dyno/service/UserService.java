package com.dyno.service;

import com.dyno.dto.UserResponse;

public interface UserService {

	UserResponse getCurrentUser(String username);
	
}
