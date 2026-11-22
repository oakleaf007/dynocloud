
package com.dyno.repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dyno.dto.DownloadTicket;

import jakarta.persistence.LockModeType;

public interface DownloadTicketRepository
        extends JpaRepository<DownloadTicket, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT t FROM DownloadTicket t
        WHERE t.tokenHash = :hash
        AND t.used = false
        AND t.expiresAt > :now
    """)
    Optional<DownloadTicket> findValidTicket(
            @Param("hash") String hash,
            @Param("now") Instant now
    );
}
