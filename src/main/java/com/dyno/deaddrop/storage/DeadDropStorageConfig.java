
package com.dyno.deaddrop.storage;

import java.net.URI;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.checksums.RequestChecksumCalculation;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

@Configuration
@EnableConfigurationProperties(DeadDropStorageProperties.class)
public class DeadDropStorageConfig {

    @Bean(name = "deadDropS3Client")
    @ConditionalOnProperty(
        prefix = "dynocloud.dead-drop.storage",
        name = "provider",
        havingValue = "garage"
    )
    public S3Client garageDeadDropS3Client(
            DeadDropStorageProperties properties) {
System.out.println("=============== Dead Drop s3 config loaded ===========");
    	AwsBasicCredentials credentials=
				AwsBasicCredentials.create(
						properties.getAccessKey(),
						properties.getSecretKey()
						);
		
		S3Configuration s3Configuration = S3Configuration.builder()
								.pathStyleAccessEnabled(true)
								.chunkedEncodingEnabled(false)
								.build();
		
		return S3Client.builder()
				.endpointOverride(URI.create(properties.getEndpoint()))
				.region(Region.of(properties.getRegion()))
				.credentialsProvider(
						StaticCredentialsProvider.create(credentials)
						)
				.serviceConfiguration(s3Configuration)
				.requestChecksumCalculation(RequestChecksumCalculation.WHEN_REQUIRED)
				.build();
		
		
    }

 

}
