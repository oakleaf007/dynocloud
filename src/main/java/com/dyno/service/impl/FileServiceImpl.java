package com.dyno.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dyno.cleanup.CleanupStatus;
import com.dyno.cleanup.StorageCleanup;
import com.dyno.cleanup.StorageCleanupRepo;
import com.dyno.dto.DecryptedFile;
import com.dyno.dto.EncryptionResult;
import com.dyno.dto.FileResponse;
import com.dyno.entity.File;
import com.dyno.entity.FileStatus;
import com.dyno.entity.User;
import com.dyno.execption.FileEncryptionException;
import com.dyno.execption.InvalidCredentialException;
import com.dyno.execption.ResourceNotFoundException;
import com.dyno.repository.StoredFileRepository;
import com.dyno.repository.UserRepository;
import com.dyno.service.EncryptionService;
import com.dyno.service.FileService;
import com.dyno.service.ObjectKeyService;
import com.dyno.service.StorageQuotaService;
import com.dyno.service.StorageService;

@Service
public class FileServiceImpl implements FileService{
	private final StorageService storageService;
	private final StoredFileRepository fileRepository;
	private final ObjectKeyService objectKeyService;
	private final UserRepository userRepository;
	private final StorageCleanupRepo cleanupRepo;
	private final StorageQuotaService storageQuotaService;
	private final EncryptionService encryptionService;
	
	public FileServiceImpl(StorageService storageService, StoredFileRepository fileRepository,
			ObjectKeyService objectKeyService, UserRepository userRepository, StorageCleanupRepo cleanupRepo, StorageQuotaService storageQuotaService, EncryptionService encryptionService) {
		super();
		this.storageService = storageService;
		this.fileRepository = fileRepository;
		this.objectKeyService = objectKeyService;
		this.userRepository = userRepository;
		this.cleanupRepo = cleanupRepo;
		this.storageQuotaService = storageQuotaService;
		this.encryptionService = encryptionService;
	}


	@Override
	public File upload(MultipartFile file, User user) throws IOException {
	
		if(file.isEmpty()) {
			throw new IllegalArgumentException("File cannot be empty");
		}
		
		
		
		UUID fileId = UUID.randomUUID();
		
		String objectKey = objectKeyService.generate(user.getId(),  fileId);

		Path tempFile = Files.createTempFile("dynocloud-", ".upload");
		
		Path encryptedTempFile = null;
		
		
		storageQuotaService.reserveQuota(user.getId(), file.getSize());
		System.out.println("file size: "+file.getSize());
		
		boolean storageFinalized =false;
		boolean storageUploaded = false;
		try {
			
			
			
			File storedFile = new File();
			
			storedFile.setId(fileId);
			storedFile.setOriginalName(file.getOriginalFilename());
			storedFile.setObjectKey(objectKey);
			storedFile.setContentType(file.getContentType());
			storedFile.setSizeBytes(file.getSize());
			storedFile.setOwner(user);
			
			
			file.transferTo(tempFile);
			try {
				EncryptionResult encryptionRes =
						encryptionService.encrypt(tempFile);
				
				encryptedTempFile = encryptionRes.getEncryptedFile();
				
				storedFile.setEncryptedDek(encryptionRes.getEncryptedDek());;
				
				storedFile.setFileIv(encryptionRes.getFileIv());
				
				storedFile.setDekIv(encryptionRes.getDekIv());
				storedFile.setEncryptionVersion(1);
			}catch(GeneralSecurityException e) {
				throw new FileEncryptionException(
						"Failed to encrypt", e
						);
			}
			
			
			
				storageService.upload(
						encryptedTempFile,
						Files.size(encryptedTempFile),
						file.getContentType(),
						objectKey
					);
			
				storageUploaded= true;
			storageQuotaService.finalizeQuota(user.getId(), file.getSize());
				storageFinalized = true;
			storedFile.setStatus(FileStatus.AVAILABLE);

				return fileRepository.save(storedFile);
				
				
		}
		catch(IOException | FileEncryptionException ex){
			
			
			        try {
			        	if(storageUploaded) {
			        		 storageService.delete(objectKey);
			        	}
			           
			            if(!storageFinalized) {
			            	 storageQuotaService.releaseQuota(user.getId(), file.getSize());
			            }
			           
			            
			        } catch (Exception cleanupEx) {
			            StorageCleanup task = new StorageCleanup();

			            task.setObjectKey(objectKey);
			            task.setStatus(CleanupStatus.PENDING);
			            task.setQuotaFinalized(true);
			            task.setUserId(user.getId());
			            task.setFileSize(file.getSize());
			            task.setAttempt(1);
			            task.setNextRetryAt(Instant.now());
			            task.setCreatedAt(Instant.now());

			            cleanupRepo.save(task);
			        }
			
			throw ex;
			
		}
		finally {
			Files.deleteIfExists(tempFile);
			
		}
	
	}


	@Override
	public Page<FileResponse> getUserFiles(String username, Pageable pageable) {
		
		User user = userRepository.findByUsername(username)
					.orElseThrow(()->
						new ResourceNotFoundException("User not found")
								);
		
		return fileRepository
				.findByOwnerIdAndStatus(user.getId(),FileStatus.AVAILABLE, pageable)
				
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
		
		System.out.println("file size: "+ file.getSizeBytes());
		storageService.delete(file.getObjectKey());
		
		
		storageQuotaService.releaseUsedQuota(user.getId(), file.getSizeBytes());
		
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


	@Override
	public DecryptedFile download(UUID fileId, User user) throws IOException {
		// TODO Auto-generated method stub
		File storedFile = fileRepository.findById(fileId)
						.orElseThrow(
								()->new ResourceNotFoundException("File not found")
								);
		if(!storedFile.getOwner().getId().equals(user.getId())) {
			throw new InvalidCredentialException(" You dont have access to this file");
		}
		
		Path encryptedTempFile = Files.createTempFile("dynocloud-download-", ".enc");
		Files.deleteIfExists(encryptedTempFile);
		Path decryptedTempFile = Files.createTempFile("dynocloud-download-",".dec");
		
		try {
			storageService.download(storedFile.getObjectKey(), encryptedTempFile);
			encryptionService.decrypt(encryptedTempFile, 
					decryptedTempFile, 
					storedFile.getEncryptedDek(), 
					storedFile.getFileIv(), 
					storedFile.getDekIv());
			
			Files.deleteIfExists(encryptedTempFile);
			
			return new DecryptedFile(decryptedTempFile,
					storedFile.getOriginalName(),
					storedFile.getContentType(),
					Files.size(decryptedTempFile)
					);
					
		}catch(GeneralSecurityException ex) {
			
			Files.deleteIfExists(decryptedTempFile);
			
			
			throw new FileEncryptionException(" Failed to decrypt", ex);
			
		}
		catch(IOException ex) {
			Files.deleteIfExists(decryptedTempFile);
			throw ex;
		}finally {
			Files.deleteIfExists(encryptedTempFile);
		}
	}
	
	


	
	
	

}
