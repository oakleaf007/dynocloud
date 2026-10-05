package com.dyno.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.dyno.entity.Storage;
import com.dyno.repository.StorageAccountRepo;
import com.dyno.service.StorageQuotaService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class StorageQuotaServiceImpl implements StorageQuotaService{

	
	private final StorageAccountRepo storageRepo;
	
	
	public StorageQuotaServiceImpl(StorageAccountRepo storageRepo) {
		super();
		this.storageRepo = storageRepo;
	}


	@Override
	public long checkQuota() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void reserveQuota(UUID userId, long fileSize) {
		// TODO Auto-generated method stub
		Storage storage = storageRepo.findByUserId(userId)
						.orElseThrow(()->
							new RuntimeException("Storage account not found")
								);
		long availableBytes = storage.getQuotaBytes()
					-storage.getUsedBytes()
					-storage.getReserveByte();
		if(fileSize > availableBytes) {
			throw new RuntimeException("Storage quota exceeded");
		}
		storage.setReserveByte(
				storage.getReserveByte()+fileSize
				);
	}


	@Override
	public void finalizeQuota(UUID userId, long fileSize) {
		// TODO Auto-generated method stub
	Storage storage = storageRepo.findByUserId(userId)
				.orElseThrow(()->
					new RuntimeException("Storage account not found")
						);
	
		storage.setReserveByte(
				storage.getReserveByte() - fileSize
				);
		
		storage.setUsedBytes(
				storage.getUsedBytes() + fileSize
				);
		
	}


	@Override
	public void releaseQuota(UUID userId, long fileSize) {
		Storage storage = storageRepo.findByUserId(userId)
				.orElseThrow(()->
					new RuntimeException("Storage account not found")
						);
		storage.setReserveByte(
				storage.getReserveByte()
				
							-fileSize
							);
	
	}


	@Override
	public void releaseUsedQuota(UUID userId, long fileSize) {
		// TODO Auto-generated method stub
		Storage storage = storageRepo.findByUserId(userId)
				.orElseThrow(()->
					new RuntimeException("Storage account not found")
						);
		storage.setUsedBytes(storage.getUsedBytes() - fileSize);
	}


	@Override
	public void reset(UUID userId) {
		// TODO Auto-generated method stub
		Storage storage = storageRepo.findByUserId(userId)
				.orElseThrow(()->
					new RuntimeException("Storage account not found")
						);
		storage.setReserveByte(0
							);
	
	}







	
	

}
