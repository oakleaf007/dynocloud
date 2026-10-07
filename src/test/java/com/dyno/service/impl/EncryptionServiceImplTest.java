package com.dyno.service.impl;

import com.dyno.config.EncryptionProperties;
import com.dyno.dto.EncryptionResult;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class EncryptionServiceImplTest {

    @Test
    void shouldEncryptFile() throws Exception {

    

        String kek =
                "0123456789abcdef"
              + "0123456789abcdef"
              + "0123456789abcdef"
              + "0123456789abcdef";

        EncryptionProperties properties =
                new EncryptionProperties();

        properties.setKek(kek);

        EncryptionServiceImpl encryptionService =
                new EncryptionServiceImpl(properties);

        Path source =
                Files.createTempFile(
                        "dynocloud-test-",
                        ".txt"
                );

        String originalContent =
                "Hello DynoCloud! This is a secret file.";

        Files.writeString(
                source,
                originalContent,
                StandardCharsets.UTF_8
        );

        // =========================
        // Act
        // =========================

        EncryptionResult result =
                encryptionService.encrypt(source);



        assertNotNull(result);

        // Encrypted file exists

        assertNotNull(result.getEncryptedFile());

        assertTrue(
                Files.exists(result.getEncryptedFile())
        );

        // Encrypted DEK exists

        assertNotNull(result.getEncryptedDek());

        assertTrue(
                result.getEncryptedDek().length > 0
        );

        // File IV exists

        assertNotNull(result.getFileIv());

        assertEquals(
                12,
                result.getFileIv().length
        );

        // DEK IV exists

        assertNotNull(result.getDekIv());

        assertEquals(
                12,
                result.getDekIv().length
        );

        // fileIv and dekIv must be different

        assertFalse(
                java.util.Arrays.equals(
                        result.getFileIv(),
                        result.getDekIv()
                )
        );

        // =========================
        // Verify ciphertext
        // =========================

        byte[] encryptedBytes =
                Files.readAllBytes(
                        result.getEncryptedFile()
                );

        assertTrue(
                encryptedBytes.length > 0
        );

        String encryptedContent =
                new String(
                        encryptedBytes,
                        StandardCharsets.UTF_8
                );

        assertNotEquals(
                originalContent,
                encryptedContent
        );

        // =========================
        // Cleanup
        // =========================

        Files.deleteIfExists(source);

        Files.deleteIfExists(
                result.getEncryptedFile()
        );
    }
}