package com.dyno.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dyno.dto.AdminLoginRequest;
import com.dyno.dto.AdminLoginResponse;
import com.dyno.service.AdminAuthService;

@RestController
@RequestMapping("/api/v1/admin/auth")
public class AdminAuthController {
	private final AdminAuthService adminAuthService;

	public AdminAuthController(AdminAuthService adminAuthService) {
		super();
		this.adminAuthService = adminAuthService;
	}
	
	@PostMapping("/login")
	public ResponseEntity<AdminLoginResponse> adminLogin( @RequestBody AdminLoginRequest adminlogin){
		AdminLoginResponse res = adminAuthService.login(adminlogin);
		
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(res);
	}

}
