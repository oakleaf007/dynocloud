package com.dyno.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private User user;

    @Column(nullable = false, unique = true)
    private String tokenHash;

    private Instant expiresAt;

    private boolean used;
    protected PasswordResetToken() {
    }
    public PasswordResetToken(
            User user,
            String tokenHash,
            Instant expiresAt) {

        this.user = user;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.used = false;
    }
    public void markAsUsed() {
	    this.used = true;
	}
	public UUID getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public String getTokenHash() {
		return tokenHash;
	}

	public Instant getExpiresAt() {
		return expiresAt;
	}

	public boolean isUsed() {
		return used;
	}
	
    
}
