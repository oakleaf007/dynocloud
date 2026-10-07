package com.dyno.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

//Mapping uploaded file information/metadata
@Entity
@Table(
		name="files",
		indexes= {
				@Index(name = "idx_file_owner", columnList="owner_id"),
				@Index(name = "idx_file_object_key", columnList= "object_key", unique=true)
		}
		)
public class File {
	
//	Id generated in the service file
	@Id
	private UUID id;
	
	@Column(name= "original_name", nullable= false)
	private String originalName;
	
	@Column(name= "object_key", nullable = false, unique=true)
	private String objectKey;
	
	@Column(name= "content_type")
	private String contentType;
	
	@Column(name="size_bytes", nullable=false)
	private long sizeBytes;
	
	@Column(name="created_at", nullable=false)
	private Instant createdAt;
	
	@Column(name="updated_at", nullable=false)
	private Instant updatedAt;
	
	

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name="owner_id", nullable = false)
	private User owner;
	
	
	@Enumerated(EnumType.STRING)
	@Column(name="status",nullable=false)
	private FileStatus status;
	
	@Column(name = "encrypted_dek", nullable = false)
	private byte[] encryptedDek;
	
	@Column(name = "file_iv", nullable = false)
	private byte[] fileIv;
	
	@Column(name = "dek_iv", nullable = false)
	private byte[] dekIv;
	
	@Column(name = "encryption_version", nullable = false)
	private Integer encryptionVersion =1 ;
	
	
	
	
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

	public Integer getEncryptionVersion() {
		return encryptionVersion;
	}

	public void setEncryptionVersion(Integer encryptionVersion) {
		this.encryptionVersion = encryptionVersion;
	}

	public FileStatus getStatus() {
		return status;
	}

	public void setStatus(FileStatus status) {
		this.status = status;
	}
	
	

	@PrePersist
	protected void onCreate() {
		Instant now = Instant.now();
		createdAt= now;
		updatedAt= now;
	}
	
	@PreUpdate
	protected void onUpdate() {
		updatedAt = Instant.now();
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

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}
	
	
}
