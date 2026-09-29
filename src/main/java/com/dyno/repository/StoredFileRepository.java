package com.dyno.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dyno.entity.File;
import com.dyno.entity.User;

public interface StoredFileRepository extends JpaRepository<File, UUID>{

	List<File> findByOwner(User owner);
	
	Optional<File> findByIdAndOwner(UUID id, User owner);
	
}
