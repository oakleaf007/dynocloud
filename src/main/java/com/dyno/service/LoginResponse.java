package com.dyno.service;

public class LoginResponse {
	private String message;
	private String accessToken;
	private String tokenType;
	
	public LoginResponse(String message , String accessToken, String tokenType ) {
		
		this.message=message;
		this.accessToken = accessToken;
		this.tokenType= tokenType;
		
	}
	
	public String getAccessToken() {
		return accessToken;
	}
	
	public String getTokenType() {
		return tokenType;
	}
	public String getMessage() {
		return message;
	}

}
