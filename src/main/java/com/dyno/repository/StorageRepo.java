package com.dyno.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.dyno.entity.Storage;

import jakarta.persistence.LockModeType;

public interface StorageRepo extends JpaRepository<Storage, UUID>{
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("""
			SELECT s from Storage s
			Where s.user.id= :userId
			""")
	Optional<Storage> findByUserIdForUpdate(UUID userId);

}
