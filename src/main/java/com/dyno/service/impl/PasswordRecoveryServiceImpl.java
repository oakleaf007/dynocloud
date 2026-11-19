package com.dyno.service.impl;

import java.time.Instant;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dyno.dto.PasswordResetReq;
import com.dyno.entity.PasswordResetToken;
import com.dyno.entity.User;
import com.dyno.repository.PasswordResetTokenRepository;

@Service
public class PasswordRecoveryServiceImpl {

    private final PasswordResetTokenRepository resetTokenRepository;
    private final PasswordResetTokenService resetTokenService;
    private final PasswordEncoder passwordEncoder;

    public PasswordRecoveryServiceImpl(
            PasswordResetTokenRepository resetTokenRepository,
            PasswordResetTokenService resetTokenService,
            PasswordEncoder passwordEncoder) {

        this.resetTokenRepository = resetTokenRepository;
        this.resetTokenService = resetTokenService;
        this.passwordEncoder = passwordEncoder;
    }


    @Transactional
    public void resetPassword(PasswordResetReq request) {

        // 1. Validate password confirmation
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new IllegalArgumentException(
                    "Passwords do not match");
        }

        // 2. Hash submitted reset token
        String tokenHash =
                resetTokenService.hashResetToken(request.resetToken());

        // 3. Find token
        PasswordResetToken resetToken =
                resetTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() ->
                    new BadCredentialsException("Invalid or expired reset token"));

        // 4. Validate token
        if (resetToken.isUsed()
                || resetToken.getExpiresAt() == null
                || !resetToken.getExpiresAt().isAfter(Instant.now())) {

            throw new BadCredentialsException(
                    "Invalid or expired reset token");
        }

        // 5. Get associated user
        User user = resetToken.getUser();

        // 6. Hash new password using BCrypt
        String encodedPassword =
                passwordEncoder.encode(request.newPassword());

        // 7. Update password
        user.setHashedPass(encodedPassword);

        // 8. Mark token as used
        resetToken.markAsUsed();
    }
}

