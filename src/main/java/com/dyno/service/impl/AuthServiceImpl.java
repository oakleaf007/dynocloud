package com.dyno.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dyno.dto.LoginReq;
import com.dyno.dto.LoginResponse;
import com.dyno.dto.RegisterReq;
import com.dyno.entity.User;
import com.dyno.execption.EmailAlreadyExistsException;
import com.dyno.execption.InvalidCredentialException;
import com.dyno.execption.UsernameAlreadyExistsException;
import com.dyno.repository.UserRepository;
import com.dyno.service.AuthService;
import com.dyno.service.JwtService;

@Service
public class AuthServiceImpl implements AuthService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	
	
	public AuthServiceImpl(UserRepository userRepository, 
			PasswordEncoder passwordEncoder,AuthenticationManager authenticationManager,
			JwtService jwtService) {
		this.userRepository=userRepository;
		this.passwordEncoder=passwordEncoder;
		this.authenticationManager=authenticationManager;
		this.jwtService=jwtService;
	}
//	sign up or registration
	public void register(RegisterReq request) {
		if(userRepository.existsByUsername(request.getUsername())) {
			throw new UsernameAlreadyExistsException("username already exists");
		}
		
		if(userRepository.existsByEmail(request.getEmail())) {
			throw new EmailAlreadyExistsException("email already exists");
		}
		
		User user = new User();
		
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		
		user.setHashedPass(passwordEncoder.encode(request.getPassword()));
		
		
		user.setRecoveryPhraseHash(passwordEncoder.encode(request.getRecoveryPhrase()));
		
		user.setEmailVerified(false);
		
		userRepository.save(user);
	
	}
	
//	login (utilizes AuthenticationManager)
	public LoginResponse login(LoginReq request) {
		
		try {
			Authentication authentication=
					authenticationManager.authenticate(
							new UsernamePasswordAuthenticationToken(
									request.getUsername(),
									request.getPassword()
									));
			String accessToken = jwtService.generateAccessToken(authentication);
			
			return new LoginResponse(
					"Login Successful",
					accessToken, "Bearer");
			
			
		}catch(BadCredentialsException ex) {
			throw new InvalidCredentialException("Invalid Credentials");
		}
	
	}

}
