package com.dyno.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.checksums.RequestChecksumCalculation;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {

	@Value("${garage.endpoint}")
	private String endpoint;
	
	@Value("${garage.region}")
	private String region;
	
	@Value("${garage.access-key}")
	private String accessKey;
	
	@Value("${garage.secret-key}")
	private String secretKey;
	
	@Bean
	public S3Client s3Client() {
		AwsBasicCredentials credentials=
				AwsBasicCredentials.create(
						accessKey,
						secretKey
						);
		
		S3Configuration s3Configuration = S3Configuration.builder()
								.pathStyleAccessEnabled(true)
								.chunkedEncodingEnabled(false)
								.build();
		
		return S3Client.builder()
				.endpointOverride(URI.create(endpoint))
				.region(Region.of(region))
				.credentialsProvider(
						StaticCredentialsProvider.create(credentials)
						)
				.serviceConfiguration(s3Configuration)
				.requestChecksumCalculation(RequestChecksumCalculation.WHEN_REQUIRED)
				.build();
		
		
	}
	

	@Bean
	public S3Presigner s3presigner() {
		AwsBasicCredentials credentials = 
				AwsBasicCredentials.create(accessKey, secretKey);
		
		S3Configuration s3Configuration = S3Configuration.builder()
				.pathStyleAccessEnabled(true)
				
				.build();
		
				return S3Presigner.builder()
						.endpointOverride(URI.create(endpoint))
						.region(Region.of(region))
						.credentialsProvider(
								StaticCredentialsProvider.create(credentials)
								)
						.serviceConfiguration(s3Configuration)
						.build();
	}	
	
}


