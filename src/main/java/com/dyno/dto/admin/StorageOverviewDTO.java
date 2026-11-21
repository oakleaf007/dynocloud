
package com.dyno.dto.admin;

public record StorageOverviewDTO(

		double totalStorageUsed,
        long totalFiles,
        long totalUsers,
        double averageStoragePerUser

) {}
