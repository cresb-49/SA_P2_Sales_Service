package com.sap.sales_service.snacks.application.ouput;

public interface SaveFilePort {
    void uploadFile(String bucketName, String directory, String keyName, byte[] fileData);
}
