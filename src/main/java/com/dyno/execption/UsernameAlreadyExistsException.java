package com.dyno.execption;

public class UsernameAlreadyExistsException extends RuntimeException{
	public UsernameAlreadyExistsException(String message) {
		super(message);
	}

}
