package com.dyno.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dyno.dto.admin.StorageOverviewDTO;
import com.dyno.entity.FileStatus;
import com.dyno.repository.FileRepository;
import com.dyno.repository.UserRepository;
import com.dyno.service.StorageAnalyticsService;
@Service
public class StorageAnalyticsServiceImpl  implements StorageAnalyticsService{


	

	    private final FileRepository fileRepository;
	    private final UserRepository userRepository;

	    public StorageAnalyticsServiceImpl(
	            FileRepository fileRepository,
	            UserRepository userRepository) {

	        this.fileRepository = fileRepository;
	        this.userRepository = userRepository;
	 
	    }
	    @Override
	    @Transactional(readOnly = true)
	    public StorageOverviewDTO getGlobalStorageOverview() {

	        List<Object[]> result =
	                fileRepository.getStorageStatistics(FileStatus.AVAILABLE);
	        long totalBytes = 0L;
	        long totalFiles = 0L;
	        
	        
	       
	        if (result != null && !result.isEmpty() && result.get(0) != null) {
	            Object[] statistics = result.get(0);
	            totalBytes = statistics[0] != null ? ((Number) statistics[0]).longValue() : 0L;
	            totalFiles = statistics[1] != null ? ((Number) statistics[1]).longValue() : 0L;
	        }
	        double totalStorageUsedGb = totalBytes / 1073741824.0;
	        long totalUsers = userRepository.count();

	        double averageStoragePerUserGb = totalUsers == 0
	                ? 0.0
	                : totalStorageUsedGb / totalUsers;
	        
	        totalStorageUsedGb = Math.round(totalStorageUsedGb * 100.0) / 100.0;
	        averageStoragePerUserGb = Math.round(averageStoragePerUserGb * 100.0) / 100.0;
	        
	        
	        return new StorageOverviewDTO(
	                totalStorageUsedGb,
	                totalFiles,
	                totalUsers,
	                averageStoragePerUserGb
	        );
	}

}
