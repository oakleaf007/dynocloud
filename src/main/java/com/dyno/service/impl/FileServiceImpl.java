package com.dyno.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dyno.dto.FileResponse;
import com.dyno.entity.File;
import com.dyno.entity.User;
import com.dyno.execption.ResourceNotFoundException;
import com.dyno.repository.StoredFileRepository;
import com.dyno.repository.UserRepository;
import com.dyno.service.FileService;
import com.dyno.service.ObjectKeyService;
import com.dyno.service.StorageService;


import software.amazon.awssdk.services.s3.model.GetObjectRequest;

@Service
public class FileServiceImpl implements FileService{
	private final StorageService storageService;
	private final StoredFileRepository fileRepository;
	private final ObjectKeyService objectKeyService;
	private final UserRepository userRepository;
	
	
	
	public FileServiceImpl(StorageService storageService, StoredFileRepository fileRepository,
			ObjectKeyService objectKeyService, UserRepository userRepository) {
		super();
		this.storageService = storageService;
		this.fileRepository = fileRepository;
		this.objectKeyService = objectKeyService;
		this.userRepository = userRepository;
	}


	@Override
	public File upload(MultipartFile file, User user) throws IOException {
	
		if(file.isEmpty()) {
			throw new IllegalArgumentException("File cannot be empty");
		}
		
		UUID fileId = UUID.randomUUID();
		
		String objectKey = objectKeyService.generate(user.getId(),  fileId);

		Path tempFile = Files.createTempFile("dynocloud-", ".upload");
		
		try {
			file.transferTo(tempFile);
		
				storageService.upload(
						tempFile,
						Files.size(tempFile),
						file.getContentType(),
						objectKey
					);
		
		}finally {
			Files.deleteIfExists(tempFile);
		}
		
		
		
		
		File storedFile = new File();
		
		storedFile.setId(fileId);
		storedFile.setOriginalName(file.getOriginalFilename());
		storedFile.setObjectKey(objectKey);
		storedFile.setContentType(file.getContentType());
		storedFile.setSizeBytes(file.getSize());
		storedFile.setOwner(user);
		
		
		return fileRepository.save(storedFile);
		
	
	}


	@Override
	public Page<FileResponse> getUserFiles(String username, Pageable pageable) {
		
		User user = userRepository.findByUsername(username)
					.orElseThrow(()->
						new ResourceNotFoundException("User not found")
								);
		
		return fileRepository
				.findByOwnerId(user.getId(), pageable)
				.map(file-> new FileResponse(
						file.getId(),
						file.getOriginalName(),
						file.getContentType(),
						file.getSizeBytes(),
						file.getCreatedAt()
						));
	}


	@Override
	public String generateDownloadUrl(String username, UUID fileId) {
		// TODO Auto-generated method stub
		
		User user = userRepository.findByUsername(username)
					.orElseThrow(()->
						new ResourceNotFoundException("User not found")
							);
		
		File file = fileRepository.findById(fileId)
					.orElseThrow(()->
						new ResourceNotFoundException("File not found")
							);
		
		if(!file.getOwner().getId().equals(user.getId())) {
			throw new ResourceNotFoundException("File not found");
			
		}
		
		return storageService.generateDownloadUrl(file.getObjectKey(), file.getContentType());
				
	}


	@Override
	public String deleteFile(String username ,UUID fileId) {
		// TODO Auto-generated method stub
	
		User user = userRepository.findByUsername(username)
					.orElseThrow(()->
						new ResourceNotFoundException("User not found")
							);
		
		File file = fileRepository.findById(fileId)
					.orElseThrow(()->
							new ResourceNotFoundException("File not found")
							);
		if(!file.getOwner().getId().equals(user.getId())) {
			throw new ResourceNotFoundException("File Not Found");
			
		}
		
		storageService.delete(file.getObjectKey());
		
		fileRepository.delete(file);
		
		return null;
	
	
	
	}


	@Override
	public FileResponse renameFile(User user, UUID fileId, String newName) {
		// TODO Auto-generated method stub
		File file = fileRepository.findByIdAndOwner(fileId, user)
					.orElseThrow(()->
					new ResourceNotFoundException("File not found")
							);
		file.setOriginalName(newName);
		
		File updatedFile = fileRepository.save(file);
		
		return new FileResponse(
				  updatedFile.getId(),
			        updatedFile.getOriginalName(),
			        updatedFile.getObjectKey(),
			        updatedFile.getContentType(),
			        updatedFile.getSizeBytes(),
			        updatedFile.getCreatedAt(),
			        updatedFile.getOwner().getId()
				);
		
		
	
	}
	
	


	
	
	

}
