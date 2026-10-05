package com.dyno.service.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;

import com.dyno.cleanup.CleanupStatus;
import com.dyno.cleanup.StorageCleanup;
import com.dyno.cleanup.StorageCleanupRepo;
import com.dyno.repository.StorageAccountRepo;
import com.dyno.service.StorageCleanupService;
import com.dyno.service.StorageQuotaService;
import com.dyno.service.StorageService;

public class CleanupServiceImpl implements StorageCleanupService {
	
	
	private final StorageCleanupRepo cleanupRepo;
	private final StorageService storageService;
	private final StorageAccountRepo storageRepo;
	private final StorageQuotaService quotaService;
	
	
	
	public CleanupServiceImpl(StorageCleanupRepo cleanupRepo, StorageService storageService, StorageAccountRepo storageRepo, StorageQuotaService quotaService) {
		super();
		this.cleanupRepo = cleanupRepo;
		this.storageService = storageService;
		this.storageRepo = storageRepo;
		this.quotaService = quotaService;
	}




	@Override
	@Scheduled(fixedDelay=60_000)
	public void cleanupTask() {
		// TODO Auto-generated method stub
		
		List<StorageCleanup> tasks =
				cleanupRepo.findTop100ByStatusAndNextRetryAtLessThanEqualOrderByCreatedAtAsc(
						CleanupStatus.PENDING, Instant.now());
		for(StorageCleanup task: tasks) {
			
			try {
				storageService.delete(task.getObjectKey());
				
				if(task.isQuotaFinalized()) {
					quotaService.releaseUsedQuota(task.getUserId(), task.getFileSize());
					
				}
				
				task.setStatus(CleanupStatus.COMPLETED);
			}catch (Exception e) {
				
				int attempt = task.getAttempt()+1;
				
				task.setAttempt(attempt);
				task.setNextRetryAt(calculateNextRetry(attempt));
			
			}
			cleanupRepo.save(task);
			
			
		}
		
	}
	
	private Instant calculateNextRetry(int attempt) {
		
		long delayMinutes = switch(attempt) {
		case 1->1;
		case 2->5;
		default->60;
		};
		
		return Instant.now().plus(Duration.ofMinutes(delayMinutes));
	}

}
