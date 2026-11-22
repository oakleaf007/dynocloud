package com.dyno.service.impl;

import java.io.InputStream;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.dyno.service.StorageService;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Service
@ConditionalOnProperty(
		name="storage.provider",
		havingValue="aws"
		)
public class AwsS3StorageImpl implements StorageService{

	private final S3Client awsS3Client;
	private final S3Presigner awsS3Presigner;
	
	public AwsS3StorageImpl(@Qualifier("awsS3Client") S3Client awsS3Client,@Qualifier("awsS3Presigner") S3Presigner awsS3Presigner ) {
		this.awsS3Client = awsS3Client;
		this.awsS3Presigner=awsS3Presigner;
	}
	@Override
	public String upload(Path file, long contentLength, String contentType, String objectKey) {
		throw new UnsupportedOperationException(
		        "AWS S3  is not implemented yet");
	}

	@Override
	public String generateDownloadUrl(String objectKey, String contentType) {
		throw new UnsupportedOperationException(
		        "AWS S3  is not implemented yet");
	}

	@Override
	public void download(String objectKey, Path destination) {
		throw new UnsupportedOperationException(
		        "AWS S3  is not implemented yet");
		
	}

	@Override
	public void delete(String objectKey) {
		throw new UnsupportedOperationException(
		        "AWS S3  is not implemented yet");
		
	}
	@Override
	public InputStream downloadStream(String objectKey) {
		// TODO Auto-generated method stub
		return null;
	}

}
