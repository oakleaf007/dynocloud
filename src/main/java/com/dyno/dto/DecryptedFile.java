package com.dyno.dto;

import java.nio.file.Path;

public class DecryptedFile {
	Path path;
	
	String fileName;
	
	String contentType;
	long size;
	public DecryptedFile(Path path, String fileName, String contentType, long size) {
		super();
		this.path = path;
		this.fileName = fileName;
		this.contentType = contentType;
		this.size = size;
	}
	public Path getPath() {
		return path;
	}
	public void setPath(Path path) {
		this.path = path;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	
	

}
