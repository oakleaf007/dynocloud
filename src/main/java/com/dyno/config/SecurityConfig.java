package com.dyno.config;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
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
	    public JwtAuthenticationConverter jwtAuthenticationConverter() {

	        JwtAuthenticationConverter converter =
	                new JwtAuthenticationConverter();

	        converter.setJwtGrantedAuthoritiesConverter(jwt -> {

	            Collection<GrantedAuthority> authorities =
	                    new ArrayList<>();

	            if ("ADMIN".equals(
	                    jwt.getClaimAsString("principal_type"))) {

	                authorities.add(
	                        new SimpleGrantedAuthority("ROLE_ADMIN")
	                );
	            }

	            return authorities;
	        });

	        return converter;
	    }

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http,JwtAuthenticationConverter jwtAuthenticationConverter)
			throws Exception {
		http
			.csrf(csrf-> csrf.disable())
			.authorizeHttpRequests(auth-> auth
					.requestMatchers("/",  "/api/v1/auth/**","/api/v1/admin/auth/**").permitAll()
					.requestMatchers("/api/v1/admin/storage/**")
					.hasRole("ADMIN")
					.anyRequest().authenticated()
					)
			.oauth2ResourceServer(oauth2->
			oauth2.jwt(jwt->jwt.jwtAuthenticationConverter(
                    jwtAuthenticationConverter
            )));
		
		return http.build();
	}

}
