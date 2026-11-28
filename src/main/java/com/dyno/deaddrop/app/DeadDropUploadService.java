
package com.dyno.deaddrop.app;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dyno.dto.EncryptionResult;
import com.dyno.service.EncryptionService;
import com.dyno.deaddrop.domain.DeadDropStatus;
import com.dyno.deaddrop.dto.DeadDropUploadRequest;
import com.dyno.deaddrop.dto.DeadDropUploadResponse;
import com.dyno.deaddrop.entity.DeadDropEntity;
import com.dyno.deaddrop.repository.DeadDropJpaRepository;
import com.dyno.deaddrop.storage.DeadDropObjectStorage;

@Service
public class DeadDropUploadService {

    private static final Logger LOGGER =
            Logger.getLogger(DeadDropUploadService.class.getName());

    private static final long EXPIRATION_SECONDS = 60L;

    // V3 = chunked AES-256-GCM encryption format.
    private static final int ENCRYPTION_VERSION = 3;

    private final DeadDropJpaRepository repository;
    private final DeadDropObjectStorage storage;
    private final DeadDropTokenService tokenService;
    private final EncryptionService encryptionService;
    private final String publicBaseUrl;
  

    public DeadDropUploadService(
            DeadDropJpaRepository repository,
            DeadDropObjectStorage storage,
            DeadDropTokenService tokenService,
            EncryptionService encryptionService,
           
            @Value("${dynocloud.dead-drop.public-base-url}")
            String publicBaseUrl
    ) {

        this.repository = repository;
        this.storage = storage;
        this.tokenService = tokenService;
        this.encryptionService = encryptionService;
        
        if (publicBaseUrl == null || publicBaseUrl.isBlank()) {
            throw new IllegalArgumentException(
                    "Dead Drop public base URL is not configured"
            );
        }

        this.publicBaseUrl = FileServiceHelper.removeTrailingSlashes(publicBaseUrl);
		
		
    }

    public DeadDropUploadResponse upload(
            DeadDropUploadRequest request
    ) throws IOException, GeneralSecurityException {

        if (request == null || request.file() == null) {
            throw new IllegalArgumentException(
                    "Upload request and file are required"
            );
        }

        MultipartFile file = request.file();

        if (file.isEmpty()) {
            throw new IllegalArgumentException(
                    "File cannot be empty"
            );
        }

      
        UUID deadDropId = UUID.randomUUID();

        String publicToken = tokenService.generateToken();

        String deletionSecret = tokenService.generateToken();

        String storageKey = UUID.randomUUID().toString();

        Path plaintextFile = null;
        Path encryptedFile = null;

        boolean objectUploaded = false;

        try {

        
            plaintextFile = Files.createTempFile(
                    "dead-drop-",
                    ".upload"
            );

           
         
            try (InputStream inputStream = file.getInputStream()) {

                Files.copy(
                        inputStream,
                        plaintextFile,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

            long originalFileSize = Files.size(plaintextFile);

            if (originalFileSize <= 0) {
                throw new IllegalArgumentException(
                        "Uploaded file is empty"
                );
            }

         
            EncryptionResult encryptionResult =
                    encryptionService.encrypt(
                            plaintextFile,
                            deadDropId
                    );

            if (encryptionResult == null
                    || encryptionResult.getEncryptedFile() == null
                    || encryptionResult.getEncryptedDek() == null
                    || encryptionResult.getFileIv() == null) {

                throw new GeneralSecurityException(
                        "Encryption did not return complete metadata"
                );
            }

            encryptedFile = encryptionResult.getEncryptedFile();

            if (!Files.isRegularFile(encryptedFile)) {
                throw new IOException(
                        "Encrypted temporary file does not exist"
                );
            }

            long encryptedFileSize = Files.size(encryptedFile);

            if (encryptedFileSize <= 0) {
                throw new IOException(
                        "Encrypted file is empty"
                );
            }

       
            try (InputStream encryptedInputStream =
                         Files.newInputStream(encryptedFile)) {

                storage.upload(
                        storageKey,
                        
                        encryptedFile
                );
            }

            objectUploaded = true;

          
            DeadDropEntity entity = new DeadDropEntity();

            entity.setId(deadDropId);

            entity.setTokenHash(
                    tokenService.hashToken(publicToken)
            );

            entity.setDeletionTokenHash(
                    tokenService.hashToken(deletionSecret)
            );

            entity.setStorageKey(storageKey);

            entity.setOriginalFilename(
            		FileServiceHelper.sanitizeFilename(request.originalFilename())
            );

            entity.setContentType(
            		FileServiceHelper.sanitizeContentType(request.contentType())
            );

            // Size of the encrypted object in the bucket.
            entity.setFileSize(encryptedFileSize);

            // Size of the original plaintext file.
            entity.setOriginalFileSize(originalFileSize);

            // Wrapped DEK from OpenBao.
            entity.setEncryptedDek(
                    encryptionResult.getEncryptedDek()
            );

            // V3 nonce prefix.
            entity.setFileIv(
                    encryptionResult.getFileIv()
            );

            // Backend-controlled encryption format.
            entity.setEncryptionVersion(ENCRYPTION_VERSION);

         
            Instant uploadedAt = Instant.now();

            entity.setCreatedAt(uploadedAt);

            entity.setExpiresAt(
                    uploadedAt.plusSeconds(EXPIRATION_SECONDS)
            );

            entity.setStatus(DeadDropStatus.ACTIVE);

         
            repository.saveAndFlush(entity);

     
            String downloadUrl =
                    publicBaseUrl + "/" + publicToken;

            return new DeadDropUploadResponse(
                    downloadUrl,
                    deletionSecret,
                    entity.getCreatedAt(),
                    entity.getExpiresAt()
            );

        } catch (IOException
                | GeneralSecurityException
                | RuntimeException exception) {

        
            if (objectUploaded) {

                try {

                    storage.delete(storageKey);

                } catch (IOException | RuntimeException cleanupException) {

                    exception.addSuppressed(cleanupException);

                    LOGGER.log(
                            Level.SEVERE,
                            "Failed to clean up Dead Drop object after upload failure. "
                                    + "Storage key: " + storageKey,
                            cleanupException
                    );
                }
            }

            throw exception;

        } finally {

           
        	FileServiceHelper.deleteTemporaryFile(plaintextFile, LOGGER);

            if (encryptedFile != null
                    && !encryptedFile.equals(plaintextFile)) {

            	FileServiceHelper.deleteTemporaryFile(encryptedFile, LOGGER);
            }
        }
    }
}

    