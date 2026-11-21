package com.dyno.dto;


public record AdminLoginResponse(
		String message,
    String accessToken,
    String tokenType,
    long expiresIn
) {}

