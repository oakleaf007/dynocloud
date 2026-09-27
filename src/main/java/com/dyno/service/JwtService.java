package com.dyno.service;

import org.springframework.security.core.Authentication;

public interface JwtService {
	String generateAccessToken(Authentication authentication);
	
	String extractUsername(String token);
	
	boolean isTokenValid(String token);

}
