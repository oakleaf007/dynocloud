
package com.dyno.deaddrop.app;

import java.io.IOException;
import static com.dyno.deaddrop.app.FileServiceHelper.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.dyno.deaddrop.domain.DeadDropStatus;
import com.dyno.deaddrop.dto.DeadDropDownloadResult;
import com.dyno.deaddrop.entity.DeadDropEntity;
import com.dyno.deaddrop.repository.DeadDropJpaRepository;
import com.dyno.deaddrop.storage.DeadDropObjectStorage;
import com.dyno.service.EncryptionService;

@Service
public class DeadDropDownloadService {

    private final DeadDropJpaRepository repository;
    private final DeadDropObjectStorage storage;
    private final DeadDropTokenService tokenService;
    private final EncryptionService encryptionService;

    public DeadDropDownloadService(
            DeadDropJpaRepository repository,
            DeadDropObjectStorage storage,
            DeadDropTokenService tokenService,
            EncryptionService encryptionService) {

        this.repository = repository;
        this.storage = storage;
        this.tokenService = tokenService;
        this.encryptionService = encryptionService;
    }

    public DeadDropDownloadResult prepareDownload(String publicToken)
            throws IOException, GeneralSecurityException {

        if (publicToken == null || publicToken.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Dead Drop not found"
            );
        }

     
        String tokenHash = tokenService.hashToken(publicToken);

       
        DeadDropEntity entity = repository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Dead Drop not found"
                ));

     
        if (entity.getStatus() != DeadDropStatus.ACTIVE) {
            throw new ResponseStatusException(
                    HttpStatus.GONE,
                    "Dead Drop is no longer available"
            );
        }

       
        if (entity.getExpiresAt() == null ||
                !entity.getExpiresAt().isAfter(Instant.now())) {

            throw new ResponseStatusException(
                    HttpStatus.GONE,
                    "Dead Drop has expired"
            );
        }

        Path plaintextFile = null;

        try {
//           create plaintext temp file
            plaintextFile = Files.createTempFile(
                    "dead-drop-download-",
                    ".tmp"
            );

           
            try (
                InputStream encryptedInput =
                        storage.download(entity.getStorageKey());

                OutputStream plaintextOutput =
                        Files.newOutputStream(plaintextFile)
            ) {

                encryptionService.decrypt(
                        encryptedInput,
                        plaintextOutput,
                        entity.getEncryptedDek(),
                        entity.getFileIv(),
                        entity.getId(),
                        entity.getOriginalFileSize()
                );
            }

            //  Verify decrypted file size.
            long actualSize = Files.size(plaintextFile);

            if (actualSize != entity.getOriginalFileSize()) {
                throw new IOException(
                        "Decrypted file size does not match stored metadata"
                );
            }

            // Return file and response metadata.
            return new DeadDropDownloadResult(
                    plaintextFile,
                    sanitizeFilename(entity.getOriginalFilename()),
                    sanitizeContentType(entity.getContentType()),
                    actualSize
            );

        } catch (IOException | GeneralSecurityException |
                 RuntimeException exception) {

            // Remove incomplete plaintext files.
            deleteTemporaryFile(plaintextFile);

            throw exception;
        }
    }
    
    private void deleteTemporaryFile(Path path) {

        if (path == null) {
            return;
        }

        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
            System.out.println(ignored);
        }

}
}
