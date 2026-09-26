package com.dyno.execption;

public class InvalidCredentialException extends RuntimeException {
	public InvalidCredentialException(String message) {
		super(message);
	}

}
