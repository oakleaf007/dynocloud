package com.dyno.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dyno.dto.PasswordResetReq;
import com.dyno.dto.RecoveryVerifyReq;
import com.dyno.service.RecoveryVerifyService;
import com.dyno.service.impl.PasswordRecoveryServiceImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class RecoveryController {
	private final RecoveryVerifyService recoveryVerifyService;
	private final PasswordRecoveryServiceImpl recoveryService;

	public RecoveryController(RecoveryVerifyService recoveryVerifyService, PasswordRecoveryServiceImpl recoveryService) {
		super();
		this.recoveryVerifyService = recoveryVerifyService;
		this.recoveryService = recoveryService;
	}


	@PostMapping("/recovery/verify")
	public ResponseEntity<?> recoveryVerify(
	        @RequestBody RecoveryVerifyReq request) {

	    String resetToken =
	            recoveryVerifyService.recoveryVerify(request);

	    return ResponseEntity.ok(
	            Map.of("resetToken", resetToken)
	    );
	}
	
	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(
	        @Valid @RequestBody PasswordResetReq request) {

	    recoveryService.resetPassword(request);

	    return ResponseEntity.ok(
	            Map.of("message", "Password reset successful")
	    );
	}
}
