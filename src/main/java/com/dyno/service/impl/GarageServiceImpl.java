package com.dyno.service.impl;

import java.io.InputStream;
import java.nio.file.Path;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dyno.service.StorageService;

import jakarta.annotation.PostConstruct;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Service
public class GarageServiceImpl implements StorageService {
	


	@Value("${garage.bucket}")
	private String bucket;
	
	private final S3Client s3Client;
	private final S3Presigner s3Presigner;
	

	public GarageServiceImpl(S3Client s3Client, S3Presigner s3Presigner) {
		
		this.s3Client = s3Client;
		this.s3Presigner = s3Presigner;
	}

	@Override
	public String upload(Path file, long contentLength, String contentType, String objectKey) {
		
		
		PutObjectRequest request = PutObjectRequest.builder()
								.bucket(bucket)
								.key(objectKey)
								.contentLength(contentLength)
								.contentType(contentType)
								.build();
		
		s3Client.putObject(
				request, 
				RequestBody.fromFile(file)
				);
		return objectKey;
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

	@Override
	public String generateDownloadUrl(String objectKey, String contentType) {
		// TODO Auto-generated method stub
		System.out.println("BUCKET = " + bucket);
	    System.out.println("OBJECT KEY = " + objectKey);


			GetObjectRequest getObjectRequest =
					GetObjectRequest.builder()
					.bucket(bucket)
					.key(objectKey)
					.build();
			
			GetObjectPresignRequest presignRequest =
					GetObjectPresignRequest.builder()
					.signatureDuration(Duration.ofMinutes(10))
					.getObjectRequest(getObjectRequest)
					.build();
			
			PresignedGetObjectRequest presignedRequest =
					s3Presigner.presignGetObject(presignRequest);
			
	

		return presignedRequest.url().toString();
	}
	
	@Override
	public void download(String objectKey, Path destination) {
		// TODO Auto-generated method stub
		
		GetObjectRequest request = GetObjectRequest.builder()
									.bucket(bucket)
									.key(objectKey)
									.build();
		s3Client.getObject(request, ResponseTransformer.toFile(destination));
		
	}
	
	
	@Override
	public void delete(String objectKey) {
		DeleteObjectRequest request = DeleteObjectRequest.builder()
					.bucket(bucket)
					.key(objectKey)
					.build();
		s3Client.deleteObject(request);
	}

	

}
