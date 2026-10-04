package com.dyno.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dyno.entity.File;
import com.dyno.entity.FileStatus;
import com.dyno.entity.User;

public interface StoredFileRepository extends JpaRepository<File, UUID>{

	Page<File> findByOwnerIdAndStatus(
			UUID ownerId,
			FileStatus status,
			Pageable pageable
		);

	
	Optional<File> findByIdAndOwner(UUID id, User owner);
	
}
