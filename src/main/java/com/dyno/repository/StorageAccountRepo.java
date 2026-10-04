package com.dyno.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dyno.entity.Storage;

public interface StorageAccountRepo extends JpaRepository<Storage, UUID>{
	
	Optional<Storage> findByUserId(UUID userId);
}
