package com.dyno.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dyno.dto.UserResponse;
import com.dyno.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

	private final UserService userService;
	public UserController(UserService userService) {
		super();
		this.userService = userService;
	}
	
	
	@GetMapping("/me")
	public ResponseEntity<UserResponse> getCurrentUser(
				Authentication authentication){
			
		String username = authentication.getName();
		
		UserResponse  response = userService.getCurrentUser(username);
		
		
		return ResponseEntity.ok(response);
	}

}
