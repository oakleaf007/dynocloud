package com.dyno.cleanup;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="storage_cleanup_task")
public class StorageCleanup {
	
	@Id
	@GeneratedValue(strategy=GenerationType.UUID)
	private UUID id;
	
	@Column(nullable = false)
	private String objectKey;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable= false)
	private CleanupStatus status;
	
	
	@Column(nullable=false)
	private boolean quotaFinalized;
	
	@Column(nullable =false)
	private UUID userId;
	
	@Column(nullable =false)
	private long fileSize;
	
	@Column(nullable= false)
	private int attempt;
	
	@Column(nullable=false)
	private Instant nextRetryAt;
	
	@Column(nullable= false)
	private Instant createdAt;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}
	
	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}
	
	
	

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String getObjectKey() {
		return objectKey;
	}

	public void setObjectKey(String objectKey) {
		this.objectKey = objectKey;
	}

	public CleanupStatus getStatus() {
		return status;
	}

	public void setStatus(CleanupStatus status) {
		this.status = status;
	}
	public boolean isQuotaFinalized() {
		return quotaFinalized;
	}


	public void setQuotaFinalized(boolean quotaFinalized) {
		this.quotaFinalized = quotaFinalized;
	}

	public int getAttempt() {
		return attempt;
	}

	public void setAttempt(int attempt) {
		this.attempt = attempt;
	}

	public Instant getNextRetryAt() {
		return nextRetryAt;
	}

	public void setNextRetryAt(Instant nextRetryAt) {
		this.nextRetryAt = nextRetryAt;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}
	
	
	

	
	
}
