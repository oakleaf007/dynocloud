
package com.dyno.dto;

import java.time.Instant;
import java.util.UUID;

import com.dyno.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "download_tickets")
public class DownloadTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private UUID fileId;

    @Column(nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean used = false;

    protected DownloadTicket() {}

    public DownloadTicket(
            User user,
            UUID fileId,
            String tokenHash,
            Instant expiresAt) {

        this.user = user;
        this.fileId = fileId;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
    }

    public void markAsUsed() {
        this.used = true;
    }

    public User getUser() {
        return user;
    }

    public UUID getFileId() {
        return fileId;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isUsed() {
        return used;
    }
}
