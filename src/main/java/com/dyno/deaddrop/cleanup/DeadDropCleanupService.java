
package com.dyno.deaddrop.cleanup;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.dyno.deaddrop.domain.DeadDropStatus;
import com.dyno.deaddrop.entity.DeadDropEntity;
import com.dyno.deaddrop.repository.DeadDropJpaRepository;
import com.dyno.deaddrop.storage.DeadDropObjectStorage;

@Service
public class DeadDropCleanupService {

    private static final Logger log =
            LoggerFactory.getLogger(DeadDropCleanupService.class);

    private final DeadDropJpaRepository repository;
    private final DeadDropObjectStorage storage;

    public DeadDropCleanupService(
            DeadDropJpaRepository repository,
            DeadDropObjectStorage storage) {

        this.repository = repository;
        this.storage = storage;
    }

    public void cleanupExpiredDrops() {

        List<DeadDropEntity> expiredDrops =
                repository
                    .findTop100ByExpiresAtLessThanEqualAndStatusInOrderByExpiresAtAsc(
                        Instant.now(),
                        List.of(
                            DeadDropStatus.ACTIVE,
                            DeadDropStatus.EXPIRED,
                            DeadDropStatus.DELETING,
                            DeadDropStatus.FAILED
                        )
                    );

        for (DeadDropEntity drop : expiredDrops) {
            deleteDrop(drop);
        }
    }

    private void deleteDrop(DeadDropEntity drop) {

        try {

            // Mark as deleting before touching storage.
            drop.setStatus(DeadDropStatus.DELETING);
            repository.save(drop);

            // Delete encrypted object from S3 / Garage.
            storage.delete(drop.getStorageKey());

            // Delete database record only after storage succeeds.
            repository.deleteById(drop.getId());

            log.info(
                "Dead Drop deleted successfully: {}",
                drop.getId()
            );

        } catch (Exception exception) {

         
            drop.setStatus(DeadDropStatus.FAILED);
            repository.save(drop);

            log.error(
                "Failed to delete Dead Drop: {}",
                drop.getId(),
                exception
            );
        }
    }
}
