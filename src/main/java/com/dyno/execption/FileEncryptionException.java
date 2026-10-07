package com.dyno.execption;

public class FileEncryptionException extends RuntimeException {
		
	public FileEncryptionException(String message, Throwable cause) {
		super (message, cause);
	}

}
