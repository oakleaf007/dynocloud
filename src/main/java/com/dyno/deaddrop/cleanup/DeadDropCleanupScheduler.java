
package com.dyno.deaddrop.cleanup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DeadDropCleanupScheduler {
	
    private static final Logger log =
            LoggerFactory.getLogger(DeadDropCleanupScheduler.class);

    private final DeadDropCleanupService cleanupService;

    public DeadDropCleanupScheduler(
            DeadDropCleanupService cleanupService) {
        this.cleanupService = cleanupService;
    }

    @Scheduled(
        fixedDelayString =
            "${dynocloud.dead-drop.cleanup-delay:60000}"
    )
    public void cleanup() {

        log.info("Starting Dead Drop cleanup");

        cleanupService.cleanupExpiredDrops();

        log.info("Dead Drop cleanup finished");
    }
}
