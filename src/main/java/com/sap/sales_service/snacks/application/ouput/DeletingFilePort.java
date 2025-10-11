package com.sap.sales_service.snacks.application.ouput;

public interface DeletingFilePort {
    void deleteFile(String bucketName, String directory, String keyName);
}
