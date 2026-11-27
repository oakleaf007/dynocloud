
package com.dyno.deaddrop.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dyno.deaddrop.domain.DeadDropStatus;
import com.dyno.deaddrop.entity.DeadDropEntity;

public interface DeadDropJpaRepository
        extends JpaRepository<DeadDropEntity, UUID> {

    Optional<DeadDropEntity> findByTokenHash(String tokenHash);

    Optional<DeadDropEntity> findByDeletionTokenHash(
        String deletionTokenHash
    );

    @Query("""
        SELECT d FROM DeadDropEntity d
        WHERE d.status = :status
        AND d.expiresAt <= :now
    """)
    List<DeadDropEntity> findExpiredDrops(
        @Param("status") DeadDropStatus status,
        @Param("now") Instant now
    );
}
