
package com.dyno.streaming.format;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

public final class ChunkedFileFormat {

    private ChunkedFileFormat() {
    }

  
    public static final byte[] MAGIC =
            "DYNCHNK3".getBytes(StandardCharsets.US_ASCII);

    public static final int VERSION = 3;

 
    public static final int CHUNK_SIZE = 1024 * 1024; // 1 MiB
    public static final int IV_LENGTH = 12;
    public static final int NONCE_PREFIX_LENGTH = 8;
    public static final int TAG_LENGTH = 16;

    public static final int HEADER_SIZE = 32;

    // Maximum number of chunks supported by a 32-bit counter.
    public static final long MAX_CHUNKS = 0x1_0000_0000L;

    public record Header(
            int version,
            int chunkSize,
            byte[] noncePrefix,
            long originalFileSize
    ) {
        public Header {
            if (version != VERSION) {
                throw new IllegalArgumentException(
                        "Unsupported encryption version"
                );
            }

            if (chunkSize != CHUNK_SIZE) {
                throw new IllegalArgumentException(
                        "Unsupported chunk size"
                );
            }

            if (noncePrefix == null ||
                    noncePrefix.length != NONCE_PREFIX_LENGTH) {
                throw new IllegalArgumentException(
                        "Invalid nonce prefix"
                );
            }

            if (originalFileSize < 0) {
                throw new IllegalArgumentException(
                        "Invalid original file size"
                );
            }

            noncePrefix = noncePrefix.clone();

            if (getChunkCount(originalFileSize) > MAX_CHUNKS) {
                throw new IllegalArgumentException(
                        "File exceeds supported chunk count"
                );
            }
        }

        @Override
        public byte[] noncePrefix() {
            return noncePrefix.clone();
        }
    }

  
    public static Header createHeader(
            byte[] noncePrefix,
            long originalFileSize
    ) {
        return new Header(
                VERSION,
                CHUNK_SIZE,
                noncePrefix,
                originalFileSize
        );
    }

   
    public static void writeHeader(
            DataOutputStream output,
            Header header
    ) throws IOException {

        output.write(MAGIC);
        output.writeInt(header.version());
        output.writeInt(header.chunkSize());
        output.write(header.noncePrefix());
        output.writeLong(header.originalFileSize());
    }

  
    public static Header readHeader(
            DataInputStream input
    ) throws IOException {

        byte[] magic = new byte[MAGIC.length];
        input.readFully(magic);

        if (!Arrays.equals(magic, MAGIC)) {
            throw new IOException(
                    "Invalid encrypted file format"
            );
        }

        int version = input.readInt();
        int chunkSize = input.readInt();

        byte[] noncePrefix = new byte[NONCE_PREFIX_LENGTH];
        input.readFully(noncePrefix);

        long originalFileSize = input.readLong();

        try {
            return new Header(
                    version,
                    chunkSize,
                    noncePrefix,
                    originalFileSize
            );
        } catch (IllegalArgumentException ex) {
            throw new IOException(
                    "Invalid encrypted file header", ex
            );
        }
    }


    public static byte[] deriveIv(
            byte[] noncePrefix,
            long chunkIndex
    ) {

        if (noncePrefix == null ||
                noncePrefix.length != NONCE_PREFIX_LENGTH) {
            throw new IllegalArgumentException(
                    "Invalid nonce prefix"
            );
        }

        if (chunkIndex < 0 || chunkIndex >= MAX_CHUNKS) {
            throw new IllegalArgumentException(
                    "Invalid chunk index"
            );
        }

        ByteBuffer buffer = ByteBuffer.allocate(IV_LENGTH)
                .order(ByteOrder.BIG_ENDIAN);

        buffer.put(noncePrefix);
        buffer.putInt((int) chunkIndex);

        return buffer.array();
    }


    public static byte[] buildAad(
            Header header,
            UUID fileId,
            long chunkIndex,
            int plaintextLength
    ) {

        if (fileId == null) {
            throw new IllegalArgumentException(
                    "File ID cannot be null"
            );
        }

        if (chunkIndex < 0 ||
                chunkIndex >= getChunkCount(header.originalFileSize())) {
            throw new IllegalArgumentException(
                    "Invalid chunk index"
            );
        }

        int expectedLength = getPlaintextChunkLength(
                header.originalFileSize(),
                chunkIndex
        );

        if (plaintextLength != expectedLength) {
            throw new IllegalArgumentException(
                    "Invalid plaintext chunk length"
            );
        }

        ByteBuffer aad = ByteBuffer.allocate(
                HEADER_SIZE + 16 + 4 + 4
        ).order(ByteOrder.BIG_ENDIAN);

        aad.put(MAGIC);
        aad.putInt(header.version());
        aad.putInt(header.chunkSize());
        aad.put(header.noncePrefix());
        aad.putLong(header.originalFileSize());

        aad.putLong(fileId.getMostSignificantBits());
        aad.putLong(fileId.getLeastSignificantBits());

        aad.putInt((int) chunkIndex);
        aad.putInt(plaintextLength);

        return aad.array();
    }

 
    public static long getChunkCount(long originalFileSize) {

        if (originalFileSize < 0) {
            throw new IllegalArgumentException(
                    "Invalid file size"
            );
        }

        if (originalFileSize == 0) {
            return 1;
        }

        return 1 + (originalFileSize - 1) / CHUNK_SIZE;
    }

 
    public static int getPlaintextChunkLength(
            long originalFileSize,
            long chunkIndex
    ) {

        long chunkCount = getChunkCount(originalFileSize);

        if (chunkIndex < 0 || chunkIndex >= chunkCount) {
            throw new IllegalArgumentException(
                    "Invalid chunk index"
            );
        }

        if (originalFileSize == 0) {
            return 0;
        }

        long offset = chunkIndex * CHUNK_SIZE;
        long remaining = originalFileSize - offset;

        return (int) Math.min(CHUNK_SIZE, remaining);
    }

  
    public static int getEncryptedChunkLength(
            long originalFileSize,
            long chunkIndex
    ) {

        return getPlaintextChunkLength(
                originalFileSize,
                chunkIndex
        ) + TAG_LENGTH;
    }
}
