package com.sap.sales_service.s3.infrastructure.input.port;

import java.io.File;
import java.io.IOException;

public interface BucketGatewayPort {
    void uploadFileFromBytes(String bucketName, String directory, String keyName, byte[] fileData);

    void uploadFileFromFile(String bucketName, String directory, String keyName, File file);

    byte[] downloadFile(String bucketName, String directory, String keyName) throws IOException;

    void deleteFile(String bucketName, String directory, String keyName);
}
