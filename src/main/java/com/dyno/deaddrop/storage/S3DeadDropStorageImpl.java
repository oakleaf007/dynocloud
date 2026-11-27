
package com.dyno.deaddrop.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Component
public class S3DeadDropStorageImpl implements DeadDropObjectStorage {

    private final S3Client s3Client;
    private final DeadDropStorageProperties properties;

    public S3DeadDropStorageImpl(
            @Qualifier("deadDropS3Client") S3Client s3Client,
            DeadDropStorageProperties properties) {

        this.s3Client = s3Client;
        this.properties = properties;
    }

    @Override
    public void upload(
            String storageKey,
            Path file) throws IOException {

        try {
            long contentLength = Files.size(file);

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(properties.getBucket())
                    .key(storageKey)
                    .contentType("application/octet-stream")
                    .contentLength(contentLength)
                    .build();

            s3Client.putObject(
                    request,
                    RequestBody.fromFile(file)
            );

        } catch (S3Exception exception) {
            throw new IOException(
                    "Failed to upload Dead Drop object to storage",
                    exception
            );
        }
    }

    @Override
    public InputStream download(String storageKey) {

        GetObjectRequest request = GetObjectRequest.builder()
            .bucket(properties.getBucket())
            .key(storageKey)
            .build();

        ResponseInputStream<GetObjectResponse> response =
            s3Client.getObject(request);

        return response;
    }

    @Override
    public void delete(String storageKey) {

        DeleteObjectRequest request = DeleteObjectRequest.builder()
            .bucket(properties.getBucket())
            .key(storageKey)
            .build();

        s3Client.deleteObject(request);
    }
}
