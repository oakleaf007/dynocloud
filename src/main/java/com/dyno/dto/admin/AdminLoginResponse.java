package com.dyno.dto.admin;


public record AdminLoginResponse(
		String message,
    String accessToken,
    String tokenType,
    long expiresIn
) {}

