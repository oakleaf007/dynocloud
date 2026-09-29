package com.dyno.service.impl;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dyno.service.StorageService;

import jakarta.annotation.PostConstruct;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class GarageServiceImpl implements StorageService {
	
	private final S3Client s3Client;

	@Value("${garage.bucket}")
	private String bucket;
	
	
	

	public GarageServiceImpl(S3Client s3Client) {
		
		this.s3Client = s3Client;
	}

	@Override
	public String upload(byte[] data, long contentLength, String contentType, String objectKey) {
		PutObjectRequest request = PutObjectRequest.builder()
								.bucket(bucket)
								.key(objectKey)
								.contentLength(contentLength)
								.contentType(contentType)
								.build();
		
		s3Client.putObject(
				request, 
				RequestBody.fromBytes(data)
				);
		return objectKey;
	}

	@Override
	public InputStream download(String objectKey) {
	GetObjectRequest request = GetObjectRequest.builder()
					.bucket(bucket)
					.key(objectKey)
					.build();
	return s3Client.getObject(request);
	}

	@Override
	public void delete(String objectKey) {
		DeleteObjectRequest request = DeleteObjectRequest.builder()
					.bucket(bucket)
					.key(objectKey)
					.build();
		s3Client.deleteObject(request);
	}
	
	@PostConstruct
	public void testGarageConnection() {

	    s3Client.headBucket(
	        HeadBucketRequest.builder()
	            .bucket("dynocloud-files")
	            .build()
	    );

	    System.out.println("===== GARAGE CONNECTION SUCCESS =====");
	}

}
