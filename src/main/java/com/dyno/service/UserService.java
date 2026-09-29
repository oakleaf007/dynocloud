package com.dyno.service;

import com.dyno.dto.UserResponse;
import com.dyno.entity.User;

public interface UserService {

	UserResponse getCurrentUser(String username);
	User getUserByUsername(String username);
	
}
