package com.dyno.service.impl;

import java.io.IOException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dyno.entity.File;
import com.dyno.entity.User;
import com.dyno.repository.StoredFileRepository;
import com.dyno.service.FileService;
import com.dyno.service.ObjectKeyService;
import com.dyno.service.StorageService;

@Service
public class FileServiceImpl implements FileService{
	private final StorageService storageService;
	private final StoredFileRepository fileRepository;
	private final ObjectKeyService objectKeyService;
	
	
	public FileServiceImpl(StorageService storageService, StoredFileRepository fileRepository,
			ObjectKeyService objectKeyService) {
		super();
		this.storageService = storageService;
		this.fileRepository = fileRepository;
		this.objectKeyService = objectKeyService;
	}


	@Override
	public File upload(MultipartFile file, User user) throws IOException {
	
		if(file.isEmpty()) {
			throw new IllegalArgumentException("File cannot be empty");
		}
		
		UUID fileId = UUID.randomUUID();
		
		String objectKey = objectKeyService.generate(user.getId(),  fileId);

		storageService.upload(
					file.getBytes(),
					file.getSize(),
					file.getContentType(),
					objectKey
				);
		
		File storedFile = new File();
		
		storedFile.setId(fileId);
		storedFile.setOriginalName(file.getOriginalFilename());
		storedFile.setObjectKey(objectKey);
		storedFile.setContentType(file.getContentType());
		storedFile.setSizeBytes(file.getSize());
		storedFile.setOwner(user);
		
		
		return fileRepository.save(storedFile);
		
	
	}
	
	

}
