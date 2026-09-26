package com.dyno.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterReq {
	
	@NotBlank
	@Size(min=3, max=50)
	private String username;
	
	@Email
	private String email;
	
	@NotBlank
	@Size(min=8, max= 128)
	private String password;
	
	@NotBlank
	@Size(min=12, max=500)
	private String recoveryPhrase;

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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRecoveryPhrase() {
		return recoveryPhrase;
	}

	public void setRecoveryPhrase(String recoveryPhrase) {
		this.recoveryPhrase = recoveryPhrase;
	}

}
