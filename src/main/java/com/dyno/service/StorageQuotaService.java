package com.dyno.service;

import java.util.UUID;

public interface StorageQuotaService {
	long checkQuota();
	
	void reserveQuota(UUID userId, long fileSize);
	
	void finalizeQuota(UUID userId, long fileSize);
	
	void releaseQuota(UUID userId, long fileSize);

	void releaseUsedQuota(UUID userId, long fileSize);

	
}
