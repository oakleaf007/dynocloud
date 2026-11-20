package com.dyno.dto;

import java.nio.file.Path;

public class EncryptionResult {

	
	Path encryptedFile;
	
	String encryptedDek;
	
	byte[] fileIv;
	
	

	public EncryptionResult(Path encryptedFile, String encryptedDek, byte[] fileIv) {
		super();
		this.encryptedFile = encryptedFile;
		this.encryptedDek = encryptedDek;
		this.fileIv = fileIv;
		
	}

	public Path getEncryptedFile() {
		return encryptedFile;
	}

	public void setEncryptedFile(Path encryptedFile) {
		this.encryptedFile = encryptedFile;
	}

	public String getEncryptedDek() {
		return encryptedDek;
	}

	public void setEncryptedDek(String encryptedDek) {
		this.encryptedDek = encryptedDek;
	}

	public byte[] getFileIv() {
		return fileIv;
	}

	public void setFileIv(byte[] fileIv) {
		this.fileIv = fileIv;
	}

	

	
	
	
	
	
}
