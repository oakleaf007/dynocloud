
package com.dyno.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import com.dyno.dto.EncryptionResult;
import com.dyno.service.EncryptionService;
import com.dyno.service.OpenBaoKmsService;
import com.dyno.streaming.format.ChunkedFileFormat;
import com.dyno.streaming.format.ChunkedFileFormat.Header;

@Service
public class EncryptionServiceImpl implements EncryptionService {

    private static final int AES_KEY_SIZE = 256;
    private static final int GCM_TAG_LENGTH = 128;

    private final SecureRandom secureRandom = new SecureRandom();

    private final OpenBaoKmsService openBaoKmsService;

    public EncryptionServiceImpl(
            OpenBaoKmsService openBaoKmsService) {

        this.openBaoKmsService = openBaoKmsService;
    }

  
    @Override
    public EncryptionResult encrypt(
            Path source,
            UUID fileId
    ) throws IOException, GeneralSecurityException {

        if (fileId == null) {
            throw new IllegalArgumentException(
                    "File ID cannot be null"
            );
        }

        if (!Files.isRegularFile(source)) {
            throw new IOException("Invalid source file");
        }

        long originalFileSize = Files.size(source);

        // Generate a fresh DEK for this file.
        SecretKey dek = generateDek();

        // Generate a random 8-byte nonce prefix.
        byte[] noncePrefix = new byte[
                ChunkedFileFormat.NONCE_PREFIX_LENGTH
        ];

        secureRandom.nextBytes(noncePrefix);

        // Wrap the DEK using OpenBao.
        byte[] dekBytes = dek.getEncoded();

        String encryptedDek;

        try {
            encryptedDek = openBaoKmsService.encryptDek(dekBytes);
        } finally {
            Arrays.fill(dekBytes, (byte) 0);
        }

        if (encryptedDek == null || encryptedDek.isBlank()) {
            throw new GeneralSecurityException(
                    "OpenBao returned an empty encrypted DEK"
            );
        }

        Header header = ChunkedFileFormat.createHeader(
                noncePrefix,
                originalFileSize
        );

        Path encryptedFile = Files.createTempFile(
                "dynocloud-", ".enc"
        );

        boolean completed = false;

        try {
            encryptFile(
                    source,
                    encryptedFile,
                    dek,
                    fileId,
                    header
            );

            completed = true;

            return new EncryptionResult(
                    encryptedFile,
                    encryptedDek,
                    noncePrefix
            );

        } finally {

            if (!completed) {
                Files.deleteIfExists(encryptedFile);
            }
        }
    }

    private SecretKey generateDek()
            throws GeneralSecurityException {

        KeyGenerator keyGenerator =
                KeyGenerator.getInstance("AES");

        keyGenerator.init(AES_KEY_SIZE, secureRandom);

        return keyGenerator.generateKey();
    }

    private void encryptFile(
            Path source,
            Path destination,
            SecretKey dek,
            UUID fileId,
            Header header
    ) throws IOException, GeneralSecurityException {
    	
        try (
            InputStream input = new BufferedInputStream(
                    Files.newInputStream(source)
            );

            DataOutputStream output = new DataOutputStream(
                    new BufferedOutputStream(
                            Files.newOutputStream(destination)
                    )
            )
        ) {

            // Write binary file header.
            ChunkedFileFormat.writeHeader(output, header);

            long originalFileSize = header.originalFileSize();

            long chunkCount =
                    ChunkedFileFormat.getChunkCount(originalFileSize);

            byte[] buffer = new byte[
                    ChunkedFileFormat.CHUNK_SIZE
            ];

            for (long chunkIndex = 0;
                    chunkIndex < chunkCount;
                    chunkIndex++) {

                int plaintextLength =
                        ChunkedFileFormat.getPlaintextChunkLength(
                                originalFileSize,
                                chunkIndex
                        );

                readExactly(
                        input,
                        buffer,
                        plaintextLength
                );

                byte[] iv = ChunkedFileFormat.deriveIv(
                        header.noncePrefix(),
                        chunkIndex
                );

                byte[] aad = ChunkedFileFormat.buildAad(
                        header,
                        fileId,
                        chunkIndex,
                        plaintextLength
                );

                Cipher cipher = Cipher.getInstance(
                        "AES/GCM/NoPadding"
                );

                GCMParameterSpec spec = new GCMParameterSpec(
                        GCM_TAG_LENGTH,
                        iv
                );

                cipher.init(
                        Cipher.ENCRYPT_MODE,
                        dek,
                        spec
                );

                cipher.updateAAD(aad);

                // Output contains ciphertext + 16-byte GCM tag.
                byte[] encryptedChunk = cipher.doFinal(
                        buffer,
                        0,
                        plaintextLength
                );

                output.write(encryptedChunk);

                Arrays.fill(encryptedChunk, (byte) 0);
            }

            // Detect a source file that changed during encryption.
            if (input.read() != -1) {
                throw new IOException(
                        "Source file changed during encryption"
                );
            }

            output.flush();
        }
    }


    @Override
    public void decrypt(
            InputStream encryptedInput,
            OutputStream plaintextOutput,
            String encryptedDek,
            byte[] fileIv,
            UUID fileId,
            long originalFileSize
    ) throws IOException, GeneralSecurityException {

        if (encryptedInput == null ||
                plaintextOutput == null ||
                fileId == null) {

            throw new IllegalArgumentException(
                    "Invalid decryption arguments"
            );
        }

        if (fileIv == null ||
                fileIv.length !=
                        ChunkedFileFormat.NONCE_PREFIX_LENGTH) {

            throw new GeneralSecurityException(
                    "Invalid file nonce prefix"
            );
        }

        if (originalFileSize < 0) {
            throw new GeneralSecurityException(
                    "Invalid original file size"
            );
        }

        // Unwrap DEK using OpenBao.
        byte[] dekBytes =
                openBaoKmsService.decryptDek(encryptedDek);

        if (dekBytes == null || dekBytes.length != 32) {
            if (dekBytes != null) {
                Arrays.fill(dekBytes, (byte) 0);
            }

            throw new GeneralSecurityException(
                    "Invalid AES-256 DEK"
            );
        }

        SecretKey dek = new SecretKeySpec(dekBytes, "AES");

        try {
            decryptChunks(
                    encryptedInput,
                    plaintextOutput,
                    dek,
                    fileIv,
                    fileId,
                    originalFileSize
            );

        } finally {
            Arrays.fill(dekBytes, (byte) 0);
        }
    }

    private void decryptChunks(
            InputStream encryptedInput,
            OutputStream plaintextOutput,
            SecretKey dek,
            byte[] expectedNoncePrefix,
            UUID fileId,
            long expectedFileSize
    ) throws IOException, GeneralSecurityException {
    	long totalDecryptedBytes = 0;
        DataInputStream input = new DataInputStream(
                new BufferedInputStream(encryptedInput)
        );

        // Read and validate header.
        Header header = ChunkedFileFormat.readHeader(input);

        if (header.originalFileSize() != expectedFileSize) {
            throw new GeneralSecurityException(
                    "Encrypted file size does not match metadata"
            );
        }

        if (!Arrays.equals(
                header.noncePrefix(),
                expectedNoncePrefix
        )) {
            throw new GeneralSecurityException(
                    "File nonce prefix mismatch"
            );
        }

        long chunkCount =
                ChunkedFileFormat.getChunkCount(expectedFileSize);

        for (long chunkIndex = 0;
                chunkIndex < chunkCount;
                chunkIndex++) {

            int plaintextLength =
                    ChunkedFileFormat.getPlaintextChunkLength(
                            expectedFileSize,
                            chunkIndex
                    );

            int encryptedLength =
                    ChunkedFileFormat.getEncryptedChunkLength(
                            expectedFileSize,
                            chunkIndex
                    );

            byte[] encryptedChunk =
                    new byte[encryptedLength];

            // Read exactly one encrypted chunk.
            input.readFully(encryptedChunk);

            byte[] iv = ChunkedFileFormat.deriveIv(
                    header.noncePrefix(),
                    chunkIndex
            );

            byte[] aad = ChunkedFileFormat.buildAad(
                    header,
                    fileId,
                    chunkIndex,
                    plaintextLength
            );

            Cipher cipher = Cipher.getInstance(
                    "AES/GCM/NoPadding"
            );

            GCMParameterSpec spec = new GCMParameterSpec(
                    GCM_TAG_LENGTH,
                    iv
            );

            cipher.init(
                    Cipher.DECRYPT_MODE,
                    dek,
                    spec
            );

            cipher.updateAAD(aad);

            byte[] plaintextChunk;

            try {
                // Authentication is verified here.
                plaintextChunk = cipher.doFinal(encryptedChunk);

            } finally {
                Arrays.fill(encryptedChunk, (byte) 0);
            }

            if (plaintextChunk.length != plaintextLength) {
                Arrays.fill(plaintextChunk, (byte) 0);

                throw new GeneralSecurityException(
                        "Invalid decrypted chunk length"
                );
            }

            // Write only after authentication succeeds.
            try {
                plaintextOutput.write(plaintextChunk);
                plaintextOutput.flush();
                
                totalDecryptedBytes += plaintextChunk.length;
            } finally {
                Arrays.fill(plaintextChunk, (byte) 0);
            }
        }
        if (totalDecryptedBytes != expectedFileSize) {
            throw new IOException(
                "Decrypted size mismatch. Expected: "
                + expectedFileSize
                + ", Actual: "
                + totalDecryptedBytes
            );
        }
        // Reject unexpected trailing bytes.
        if (input.read() != -1) {
            throw new IOException(
                    "Unexpected trailing data in encrypted file"
            );
        }

        plaintextOutput.flush();
    }


    private void readExactly(
            InputStream input,
            byte[] buffer,
            int length
    ) throws IOException {

        int totalRead = 0;

        while (totalRead < length) {

            int bytesRead = input.read(
                    buffer,
                    totalRead,
                    length - totalRead
            );

            if (bytesRead == -1) {
                throw new EOFException(
                        "Source file ended unexpectedly"
                );
            }

            if (bytesRead == 0) {
                continue;
            }

            totalRead += bytesRead;
        }
    }
}
