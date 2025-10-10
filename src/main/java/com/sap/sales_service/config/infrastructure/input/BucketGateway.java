package com.sap.sales_service.config.infrastructure.input;

import com.sap.sales_service.config.infrastructure.input.port.BucketGatewayPort;
import com.sap.sales_service.config.infrastructure.output.adapter.S3ServicePort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;

@Component
@AllArgsConstructor
@Transactional
public class BucketGateway implements BucketGatewayPort {

    private final S3ServicePort s3ServicePort;

    @Override
    public void uploadFileFromBytes(String bucketName, String directory, String keyName, byte[] fileData) {
        s3ServicePort.uploadFileFromBytes(bucketName, directory, keyName, fileData);
    }

    @Override
    public void uploadFileFromFile(String bucketName, String directory, String keyName, File file) {
        s3ServicePort.uploadFileFromFile(bucketName, directory, keyName, file);
    }

    @Override
    public byte[] downloadFile(String bucketName, String directory, String keyName) throws IOException {
        return s3ServicePort.downloadFile(bucketName, directory, keyName);
    }

    @Override
    public void deleteFile(String bucketName, String directory, String keyName) {
        s3ServicePort.deleteFile(bucketName, directory, keyName);
    }
}
