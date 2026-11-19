package com.dyno.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dyno.config.StorageProperties;
import com.dyno.dto.LoginReq;
import com.dyno.dto.LoginResponse;
import com.dyno.dto.RegisterReq;
import com.dyno.dto.RegisterRes;
import com.dyno.entity.Storage;
import com.dyno.entity.User;
import com.dyno.execption.EmailAlreadyExistsException;
import com.dyno.execption.InvalidCredentialException;
import com.dyno.execption.UsernameAlreadyExistsException;
import com.dyno.repository.StorageAccountRepo;
import com.dyno.repository.UserRepository;
import com.dyno.service.AuthService;
import com.dyno.service.JwtService;
import com.dyno.service.RecoveryPhraseService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final StorageProperties storageProperties;
	private final StorageAccountRepo storageRepo;
	private final RecoveryPhraseService recoveryPhraseService;
	
	public AuthServiceImpl(UserRepository userRepository, 
			PasswordEncoder passwordEncoder,AuthenticationManager authenticationManager,
			JwtService jwtService, StorageProperties storageProperties, StorageAccountRepo storageRepo, RecoveryPhraseService recoveryPhraseService) {
		this.userRepository=userRepository;
		this.passwordEncoder=passwordEncoder;
		this.authenticationManager=authenticationManager;
		this.jwtService=jwtService;
		this.storageProperties = storageProperties;
		this.storageRepo = storageRepo;
		this.recoveryPhraseService = recoveryPhraseService;
	}
//	sign up or registration
	public RegisterRes register(RegisterReq request) {
		if(userRepository.existsByUsername(request.getUsername())) {
			throw new UsernameAlreadyExistsException("username already exists");
		}
		if(request.getEmail() !=null && !request.getEmail().isBlank())
		if(userRepository.existsByEmail(request.getEmail())) {
			throw new EmailAlreadyExistsException("email already exists");
		}
		
		User user = new User();
		
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		
		user.setHashedPass(passwordEncoder.encode(
				request.getPassword()
				));
		
//		generate recovery pharse
		
		String recoveryPhrase =
		        recoveryPhraseService.generatePhrase();
		
		String normalizedPhrase = recoveryPhraseService.normalize(recoveryPhrase);
		String recoveryHash =
		        passwordEncoder.encode( recoveryPhraseService.toSha256Base64(recoveryPhrase));
		user.setRecoveryPhraseHash(recoveryHash);
		
		user.setEmailVerified(false);
		
		User savedUser=userRepository.save(user);
		Storage storage = new Storage();
		
		storage.setUser(savedUser);
		storage.setQuotaBytes(storageProperties.getDefaultQuotaBytes());
		
		storage.setUsedBytes(0L);
		
		storageRepo.save(storage);
		
		return new RegisterRes(
				"Registration successfull",
				user.getUsername(),
				recoveryPhrase
				);
	}
	
//	login (uses AuthenticationManager)
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
