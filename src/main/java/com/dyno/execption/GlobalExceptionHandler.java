package com.dyno.execption;

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
}
