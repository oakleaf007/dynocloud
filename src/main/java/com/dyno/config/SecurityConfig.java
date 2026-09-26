package com.dyno.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(12);
	}
	@Bean
	public AuthenticationManager authenticationManager(
			AuthenticationConfiguration configuration)
	throws Exception{
		return configuration.getAuthenticationManager();
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http)
			throws Exception {
		http
			.csrf(csrf-> csrf.disable())
			.authorizeHttpRequests(auth-> auth
					.requestMatchers("/api/v1/auth/register", "/api/v1/auth/login").permitAll()
					.anyRequest().authenticated()
					)
			.oauth2ResourceServer(oauth2->
			oauth2.jwt(jwt->{}));
		
		return http.build();
	}

}
