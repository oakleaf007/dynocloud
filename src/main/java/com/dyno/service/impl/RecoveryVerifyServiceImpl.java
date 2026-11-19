package com.dyno.service.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dyno.dto.RecoveryVerifyReq;
import com.dyno.entity.PasswordResetToken;
import com.dyno.entity.User;
import com.dyno.repository.PasswordResetTokenRepository;
import com.dyno.repository.UserRepository;
import com.dyno.service.RecoveryVerifyService;

@Service
public class RecoveryVerifyServiceImpl implements RecoveryVerifyService{
	
	private final UserRepository userRepository;
	private final PasswordEncoder recoveryEncoder;
	private final RecoveryPhraseServiceImpl recoveryPhraseService;
	private final PasswordResetTokenService resetTokenService;
	private final PasswordResetTokenRepository resetTokenRepository;
	public RecoveryVerifyServiceImpl(UserRepository userRepository, PasswordEncoder recoveryEncoder, RecoveryPhraseServiceImpl recoveryPhraseService, PasswordResetTokenService resetTokenService, PasswordResetTokenRepository resetTokenRepository) {
		super();
		this.userRepository = userRepository;
		this.recoveryEncoder = recoveryEncoder;
		this.recoveryPhraseService = recoveryPhraseService;
		this.resetTokenService = resetTokenService;
		this.resetTokenRepository = resetTokenRepository;
		
	}


	@Override
	@Transactional
	public String recoveryVerify(RecoveryVerifyReq request) {
		User user = userRepository
			    .findByUsername(request.username())
			    .orElseThrow(() ->
			        new BadCredentialsException("Invalid credentials"));
		 String normalized = recoveryPhraseService.normalize(request.recoveryPhrase());
		    String sha256Input = recoveryPhraseService.toSha256Base64(normalized);
			boolean valid = recoveryEncoder.matches(
			   sha256Input,
			    user.getRecoveryPhraseHash()
			);

			if (!valid) {
			    throw new BadCredentialsException("Invalid credentials");
			}
			
			 String resetToken = resetTokenService.generateResetToken();
			 
			 String tokenHash =resetTokenService.hashResetToken(resetToken);

		       
		        // 5. Invalidate previous reset tokens
		        resetTokenRepository.deleteAllByUser(user);

		        // 6. Create new reset token
		        PasswordResetToken token = new PasswordResetToken(
		                user,
		                tokenHash,
		                Instant.now().plus(10, ChronoUnit.MINUTES)
		        );

		        // 7. Save token hash
		        resetTokenRepository.save(token);

		        // 8. Return original token
		        return resetToken;
		    }
		
	}


