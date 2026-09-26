package com.dyno.service.impl;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.dyno.service.JwtService;

@Service
public class JwtServiceImpl implements JwtService{
	
 private final JwtEncoder jwtEncoder;
 
    
	private final String issuer;
	
	
	private final long accessTokenExpiration;
 
 	public JwtServiceImpl(
 			JwtEncoder jwtEncoder,
 			@Value("${jwt.issuer}")
 	        String issuer,
 	        @Value("${jwt.access-token-expiration}")
 	        long accessTokenExpiration) {
 		
 		this.jwtEncoder = jwtEncoder;
 		this.issuer=issuer;
 		this.accessTokenExpiration=accessTokenExpiration;
 		
 		
 	}
 
	@Override
	public String generateAccessToken(Authentication authentication) {
		Instant now = Instant.now();
		
		JwtClaimsSet claims = JwtClaimsSet.builder()
							.issuer(issuer)
							.subject(authentication.getName())
							.issuedAt(now)
							.expiresAt(now.plusSeconds(accessTokenExpiration))
							.build();
		return jwtEncoder
				.encode(JwtEncoderParameters.from(claims))
				.getTokenValue();
							
				
	}

	@Override
	public String extractusername(String token) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isTokenValid(String token) {
		// TODO Auto-generated method stub
		return false;
	}
	
}