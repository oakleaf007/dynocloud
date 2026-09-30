package com.dyno.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dyno.entity.File;

public interface FileRepository extends JpaRepository<File, UUID>{

	Page<File> findByOwnerId(
				UUID ownerId,
				Pageable pageable
			);
	
}
