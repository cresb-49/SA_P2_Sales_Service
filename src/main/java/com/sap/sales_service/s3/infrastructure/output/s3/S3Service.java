package com.sap.sales_service.s3.infrastructure.output.s3;

import com.sap.sales_service.s3.infrastructure.output.adapter.S3ServicePort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.IOException;

@Service
@AllArgsConstructor
public class S3Service implements S3ServicePort {
    private final S3Client s3Client;

    @Override
    public void uploadFileFromBytes(String bucketName, String directory, String keyName, byte[] fileData) {
        String key = (directory.endsWith("/") ? directory : directory + "/") + keyName;
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromBytes(fileData));
    }

    @Override
    public void uploadFileFromFile(String bucketName, String directory, String keyName, File file) {
        String key = (directory.endsWith("/") ? directory : directory + "/") + keyName;
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromFile(file));
    }

    @Override
    public byte[] downloadFile(String bucketName, String directory, String keyName) throws IOException {
        String key = (directory.endsWith("/") ? directory : directory + "/") + keyName;
        return s3Client.getObject(builder -> builder.bucket(bucketName).key(key))
                .readAllBytes();
    }

    @Override
    public void deleteFile(String bucketName, String directory, String keyName) {
        String key = (directory.endsWith("/") ? directory : directory + "/") + keyName;
        DeleteObjectRequest req = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3Client.deleteObject(req);
    }
}
