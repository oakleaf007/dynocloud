
package com.dyno.deaddrop.repository;

import java.time.Instant;
import java.util.Collection;
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
    	    WHERE d.expiresAt <= :now
    	    AND d.status IN :statuses
    	""")
    	List<DeadDropEntity> findExpiredDrops(
    	        @Param("statuses") Collection<DeadDropStatus> statuses,
    	        @Param("now") Instant now
    	);
    List<DeadDropEntity>
    findTop100ByExpiresAtLessThanEqualAndStatusInOrderByExpiresAtAsc(
            Instant now,
            Collection<DeadDropStatus> statuses
    );	
}
