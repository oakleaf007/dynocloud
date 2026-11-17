package com.dyno.execption;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(UsernameAlreadyExistsException.class)
	public ResponseEntity<String> handleUsernameExists(
			UsernameAlreadyExistsException ex){
		return ResponseEntity
					.status(HttpStatus.CONFLICT)
					.body(ex.getMessage());
		
	}
	
	@ExceptionHandler(EmailAlreadyExistsException.class)
	public ResponseEntity<String> handleEmailExists(
			EmailAlreadyExistsException ex){
		return ResponseEntity
					.status(HttpStatus.CONFLICT)
					.body(ex.getMessage());
	}
	
	@ExceptionHandler(InvalidCredentialException.class)
	public ResponseEntity<String> handleInvalidCredential(
			InvalidCredentialException ex){
		return ResponseEntity
					.status(HttpStatus.UNAUTHORIZED)
					.body(ex.getMessage());
	}
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<String> handleNotFoundException(
			ResourceNotFoundException ex
			){
		return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.body(ex.getMessage());
		
	}
	public ResponseEntity<String> handleEncryptionException(
			FileEncryptionException ex){
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(ex.getMessage());
	}
	
	@ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<Map<String, Object>> handleUnsupportedOperation(
            UnsupportedOperationException ex) {

        Map<String, Object> response = Map.of(
                "timestamp", Instant.now().toString(),
                "status", 501,
                "error", "Not Implemented",
                "message", ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_IMPLEMENTED)
                .body(response);
    }
	
	
}
