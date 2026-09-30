package com.dyno.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dyno.dto.LoginReq;
import com.dyno.dto.LoginResponse;
import com.dyno.dto.RegisterReq;
import com.dyno.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
	private final AuthService authService;
	
	
	public AuthController(AuthService authService) {
		this.authService = authService;
	}
	
	
	@PostMapping("/register")
	public ResponseEntity<String> register(@Valid @RequestBody RegisterReq request){
		authService.register(request);
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body("Registration successful");
	}
	
	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginReq request){
		
		
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(authService.login(request));
	}

}
