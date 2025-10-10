package com.sap.sales_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {
    
    private static final Logger logger = LoggerFactory.getLogger(S3Config.class);
    
    @Value("${aws.s3.access-key}")
    private String accessKey;
    @Value("${aws.s3.secret-key}")
    private String secretKey;
    @Value("${aws.region}")
    private String region;

    @Bean
    public S3Client s3Client(){
        logger.info("Configurando S3Client con:");
        logger.info("Access Key: {}", accessKey != null ? "***" + accessKey.substring(Math.max(0, accessKey.length() - 4)) : "null");
        logger.info("Secret Key: {}", secretKey != null ? "***configurado" : "null");
        logger.info("Region: {}", region);
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);
        return S3Client.builder()
                .credentialsProvider(() -> awsCreds)
                .region(software.amazon.awssdk.regions.Region.of(region))
                .build();
    }

}
