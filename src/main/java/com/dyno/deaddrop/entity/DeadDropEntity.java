
package com.dyno.deaddrop.entity;

import java.time.Instant;
import java.util.UUID;

import com.dyno.deaddrop.domain.DeadDropStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "dead_drops",
    schema = "dead_drop",
    indexes = {
        @Index(
            name = "idx_dead_drop_expires_at",
            columnList = "expires_at"
        ),
        @Index(
            name = "idx_dead_drop_status_expires",
            columnList = "status, expires_at"
        )
    }
)
public class DeadDropEntity {

    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(
        name = "token_hash",
        nullable = false,
        unique = true,
        length = 64
    )
    private String tokenHash;

    @Column(
        name = "deletion_token_hash",
        nullable = false,
        unique = true,
        length = 64
    )
    private String deletionTokenHash;

    @Column(
        name = "storage_key",
        nullable = false,
        unique = true,
        length = 255
    )
    private String storageKey;

    @Column(
        name = "original_filename",
        nullable = false,
        length = 255
    )
    private String originalFilename;

    @Column(
        name = "content_type",
        length = 150
    )
    private String contentType;

    @Column(
        name = "file_size",
        nullable = false
    )
    private Long fileSize;

    @Column(
        name = "encryption_version",
        nullable = false
    )
    private Integer encryptionVersion;

    @Column(
        name = "chunk_size"
    )
    private Integer chunkSize;

    @Column(
    	    name = "encrypted_dek",
    	    nullable = false,
    	    columnDefinition = "text"
    	)
    	private String encryptedDek;

    	@Column(
    	    name = "file_iv",
    	    nullable = false,
    	    columnDefinition = "bytea"
    	)
    	private byte[] fileIv;

    	@Column(
    	    name = "original_file_size",
    	    nullable = false
    	)
    	private Long originalFileSize;
    
    
    @Column(
        name = "created_at",
        nullable = false,
        updatable = false
    )
    private Instant createdAt;

    @Column(
        name = "expires_at",
        nullable = false
    )
    private Instant expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(
        name = "status",
        nullable = false,
        length = 20
    )
    private DeadDropStatus status;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (status == null) {
            status = DeadDropStatus.UPLOADING;
        }
     
    }

    public DeadDropEntity() {
    }

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getTokenHash() {
		return tokenHash;
	}

	public void setTokenHash(String tokenHash) {
		this.tokenHash = tokenHash;
	}

	public String getDeletionTokenHash() {
		return deletionTokenHash;
	}

	public void setDeletionTokenHash(String deletionTokenHash) {
		this.deletionTokenHash = deletionTokenHash;
	}

	public String getStorageKey() {
		return storageKey;
	}

	public void setStorageKey(String storageKey) {
		this.storageKey = storageKey;
	}

	public String getOriginalFilename() {
		return originalFilename;
	}

	public void setOriginalFilename(String originalFilename) {
		this.originalFilename = originalFilename;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public Integer getEncryptionVersion() {
		return encryptionVersion;
	}

	public void setEncryptionVersion(Integer encryptionVersion) {
		this.encryptionVersion = encryptionVersion;
	}

	public Integer getChunkSize() {
		return chunkSize;
	}

	public void setChunkSize(Integer chunkSize) {
		this.chunkSize = chunkSize;
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

	public Long getOriginalFileSize() {
		return originalFileSize;
	}

	public void setOriginalFileSize(Long originalFileSize) {
		this.originalFileSize = originalFileSize;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public Instant getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(Instant expiresAt) {
		this.expiresAt = expiresAt;
	}

	public DeadDropStatus getStatus() {
		return status;
	}

	public void setStatus(DeadDropStatus status) {
		this.status = status;
	}

  
}
