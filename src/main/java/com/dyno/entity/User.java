package com.dyno.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name="users")
public class User {

	@Id
	@GeneratedValue(strategy=GenerationType.UUID)
	private UUID id;
	
	@Column(nullable = false, unique = true, length = 100)
	private String username;
	
	@Column(unique = true)
	private String email;
	
	@Column(nullable = false)
	private String hashedPass;
	
	@Column(nullable =  false)
	private String recoveryPhraseHash;
	
	@Column(nullable = false)
	private boolean emailVerified = false;
	
	@Column(nullable = false)
	private LocalDateTime createdAt;
	
	@Column(nullable=false)
	private LocalDateTime updatedAt;
	
	@PrePersist
	protected void onCreate() {
		LocalDateTime now = LocalDateTime.now();
		createdAt=now;
		updatedAt=now;
	}
	
	@PreUpdate
	protected void onUpdate() {
		
		updatedAt = LocalDateTime.now();
	}

	public UUID getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getHashedPass() {
		return hashedPass;
	}

	public void setHashedPass(String hashedPass) {
		this.hashedPass = hashedPass;
	}

	public String getRecoveryPhraseHash() {
		return recoveryPhraseHash;
	}

	public void setRecoveryPhraseHash(String recoveryPhraseHash) {
		this.recoveryPhraseHash = recoveryPhraseHash;
	}

	public boolean isEmailVerified() {
		return emailVerified;
	}

	public void setEmailVerified(boolean emailVerified) {
		this.emailVerified = emailVerified;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	
}
