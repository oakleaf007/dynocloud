package com.dyno.config;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.cors.CorsConfigurationSource;

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
	public SecurityFilterChain securityFilterChain(HttpSecurity http,
			JwtAuthenticationConverter jwtAuthenticationConverter,
			@Qualifier("corsConfigSource") CorsConfigurationSource  corsConfigurationSource
			)
			throws Exception {
		http
			.cors(cors->cors.configurationSource(corsConfigurationSource))
			.csrf(csrf-> csrf.disable())
			.authorizeHttpRequests(auth-> auth
					.requestMatchers("/",  "/api/v1/auth/**","/api/v1/admin/auth/**", "/api/v1/files/**").permitAll()
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
	
	@Bean
	public SecurityContextRepository securityContexxtRepository() {
		return new HttpSessionSecurityContextRepository();
	}
	
//	security filter chain for session based login
	
	@Bean
	@Order(1)
	public SecurityFilterChain sessionSecurityFilterChain(
			HttpSecurity http,
			SecurityContextRepository securityContextRepository,
			@Qualifier("corsConfigSource")
			CorsConfigurationSource corsConfigurationSource
			) throws Exception {
		
		
		http
		.securityMatcher("/api/v1/session/**")
		.cors(cors->cors.configurationSource(corsConfigurationSource))
		.securityContext(context -> context.securityContextRepository(securityContextRepository))
		.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
		.authorizeHttpRequests(auth -> 
		auth.requestMatchers("/api/v1/session/login", "/api/v1/session/csrf").permitAll()
			.anyRequest().authenticated()
				);
		
		return http.build();
	}

}
