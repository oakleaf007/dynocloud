package com.dyno.repository;



import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dyno.entity.PasswordResetToken;
import com.dyno.entity.User;

public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, UUID> {

    void deleteAllByUser(User user);


    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

}