package com.dyno.service;

import org.springframework.security.core.Authentication;

public interface JwtService {
	String generateAccessToken(Authentication authentication);
	
	String extractusername(String token);
	
	boolean isTokenValid(String token);

}
