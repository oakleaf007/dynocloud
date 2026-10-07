package com.dyno.dto;

import java.nio.file.Path;

public class EncryptionResult {

	
	Path encryptedFile;
	
	byte[] encryptedDek;
	
	byte[] fileIv;
	
	byte[] dekIv;

	public EncryptionResult(Path encryptedFile, byte[] encryptedDek, byte[] fileIv, byte[] dekIv) {
		super();
		this.encryptedFile = encryptedFile;
		this.encryptedDek = encryptedDek;
		this.fileIv = fileIv;
		this.dekIv = dekIv;
	}

	public Path getEncryptedFile() {
		return encryptedFile;
	}

	public void setEncryptedFile(Path encryptedFile) {
		this.encryptedFile = encryptedFile;
	}

	public byte[] getEncryptedDek() {
		return encryptedDek;
	}

	public void setEncryptedDek(byte[] encryptedDek) {
		this.encryptedDek = encryptedDek;
	}

	public byte[] getFileIv() {
		return fileIv;
	}

	public void setFileIv(byte[] fileIv) {
		this.fileIv = fileIv;
	}

	public byte[] getDekIv() {
		return dekIv;
	}

	public void setDekIv(byte[] dekIv) {
		this.dekIv = dekIv;
	}

	
	
	
	
	
}
