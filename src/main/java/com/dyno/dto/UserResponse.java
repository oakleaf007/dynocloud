package com.dyno.dto;

public class UserResponse {
private String username;
private String email;
private long storageQoata;
private long usedQuota;


	
	
	public UserResponse(String username, String email, long storageQoata, long usedQuota) {
	super();
	this.username = username;
	this.email = email;
	this.storageQoata = storageQoata;
	this.usedQuota = usedQuota;
}
	public String getUsername() {
		return username;
	}
	public String getEmail() {
		return email;
	}
	public long getStorageQoata() {
		return storageQoata;
	}
	public void setStorageQoata(long storageQoata) {
		this.storageQoata = storageQoata;
	}
	public long getUsedQuota() {
		return usedQuota;
	}
	public void setUsedQuota(long usedQuota) {
		this.usedQuota = usedQuota;
	}
	
	
}
