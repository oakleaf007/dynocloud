package com.dyno.cleanup;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StorageCleanupRepo extends JpaRepository<StorageCleanup, UUID>{
List<StorageCleanup> findTop100ByStatusAndNextRetryAtLessThanEqualOrderByCreatedAtAsc(
		
		CleanupStatus status,
		Instant now
		
		);
}
