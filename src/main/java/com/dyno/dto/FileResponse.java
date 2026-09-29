package com.dyno.dto;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

public class FileResponse {
	
	@NotBlank
    UUID id;
    
	@NotBlank
    String originalName;
	
	@NotBlank
    String objectKey;
	
	@NotBlank
    String contentType;
	
	
	@NotBlank
    long sizeBytes;
    
	@NotBlank
    Instant createdAt;
	
	@NotBlank
	UUID ownerId;
	
	

	
    
	public FileResponse(@NotBlank UUID id, @NotBlank String originalName, @NotBlank String objectKey,
			@NotBlank String contentType, @NotBlank long sizeBytes, @NotBlank Instant createdAt, UUID ownerId) {
		super();
		this.id = id;
		this.originalName = originalName;
		this.objectKey = objectKey;
		this.contentType = contentType;
		this.sizeBytes = sizeBytes;
		this.createdAt = createdAt;
		this.ownerId = ownerId;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getOriginalName() {
		return originalName;
	}

	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	public String getObjectKey() {
		return objectKey;
	}

	public void setObjectKey(String objectKey) {
		this.objectKey = objectKey;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public long getSizeBytes() {
		return sizeBytes;
	}

	public void setSizeBytes(long sizeBytes) {
		this.sizeBytes = sizeBytes;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public UUID getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(UUID ownerId) {
		this.ownerId = ownerId;
	}

	
}
