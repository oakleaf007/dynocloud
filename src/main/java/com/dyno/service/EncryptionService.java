
package com.dyno.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.UUID;

import com.dyno.dto.EncryptionResult;

public interface EncryptionService {

    EncryptionResult encrypt(Path source, UUID fileId)
            throws IOException, GeneralSecurityException;

    void decrypt(
            InputStream encryptedInput,
            OutputStream plaintextOutput,
            String encryptedDek,
            byte[] fileIv,
            UUID fileId,
            long originalFileSize
    ) throws IOException, GeneralSecurityException;
}
