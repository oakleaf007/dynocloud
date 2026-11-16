package com.dyno.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dyno.entity.User;
import com.dyno.execption.ResourceNotFoundException;
import com.dyno.repository.StoredFileRepository;
import com.dyno.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {
	
	@Mock
	private StoredFileRepository fileRepo;
	
	@Mock
	private UserRepository userRepo;
	
	@Mock
	private GarageServiceImpl storageService;
	  
	@Mock
	    private StorageQuotaServiceImpl storageQuotaService;
	  
	@InjectMocks
	private FileServiceImpl fileService;

	@Test
	void ShouldDeleteFile() {
		
	
	User user = new User();
	user.setId(UUID.randomUUID());
	
	UUID fileId= UUID.randomUUID();
//	File file = new File();
//	file.setObjectKey("users/123/files/69");
//	file.setOwner(user);
//	file.setSizeBytes(100L);
	
	
	when(userRepo.findByUsername("123"))
		.thenReturn(Optional.of(user));
	
	when(fileRepo.findById(fileId))
	     .thenReturn(Optional.empty());
	
	assertThrows(
			ResourceNotFoundException.class,
			()-> fileService.deleteFile("123",fileId)
			);
	
	 verify(userRepo).findByUsername("123");
	verify(fileRepo).findById(any());
	verify(storageService, never()).delete("users/123/files/69");
//	verify(storageQuotaService)
//       .releaseUsedQuota(user.getId(), 100L);
	verify(fileRepo, never()).delete(any());
		
	
	}
}
