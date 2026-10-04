package com.dyno.service;

public interface StorageQuotaService {
	long checkQuota();
	
	long reserveQuota();
	
	long finalizeQuota();
	
	long releaseQuota();
}
