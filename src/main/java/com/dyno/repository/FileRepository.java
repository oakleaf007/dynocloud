package com.dyno.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dyno.entity.File;
import com.dyno.entity.FileStatus;

public interface FileRepository extends JpaRepository<File, UUID>{

	Page<File> findByOwnerId(
				UUID ownerId,
				Pageable pageable
			);
	
	 @Query("""
	            SELECT COALESCE(SUM(f.sizeBytes), 0), COUNT(f)
	            FROM File f
	            WHERE f.status = :status
	            """)
	   List<Object[]>getStorageStatistics(
	            @Param("status") FileStatus status
	    );
	
}
