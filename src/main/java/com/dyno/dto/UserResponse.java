package com.dyno.dto;

public class UserResponse {
private String username;
private String email;


	public UserResponse(String username, String email) {
		this.username=username;
		this.email=email;
	}
	
	public String getUsername() {
		return username;
	}
	public String getEmail() {
		return email;
	}
}
