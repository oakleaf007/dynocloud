package com.dyno.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="storage")
public class Storage {
	
	@Id
	@GeneratedValue(strategy= GenerationType.UUID)
	private UUID id;
	
	@OneToOne
	@JoinColumn(name="user_id", nullable = false, unique=true)
	private User user;
	
	@Column(nullable=false)
	private long quotaBytes;
	
	
	@Column(nullable=false)
	private long usedBytes=0L;
	
	@Column(nullable=false)
	private long reserveQuota=0L;


	public UUID getId() {
		return id;
	}


	public void setId(UUID id) {
		this.id = id;
	}


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}


	public long getQuotaBytes() {
		return quotaBytes;
	}


	public void setQuotaBytes(long quotaBytes) {
		this.quotaBytes = quotaBytes;
	}


	public long getUsedBytes() {
		return usedBytes;
	}


	public void setUsedBytes(long usedBytes) {
		this.usedBytes = usedBytes;
	}
	
	
}
