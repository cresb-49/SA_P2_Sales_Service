package com.sap.sales_service.snacks.infrastructure.output.bucket;

import com.sap.sales_service.config.infrastructure.input.port.BucketGatewayPort;
import com.sap.sales_service.snacks.application.ouput.DeletingFilePort;
import com.sap.sales_service.snacks.application.ouput.SaveFilePort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class BucketAdapter implements SaveFilePort, DeletingFilePort {

    private final BucketGatewayPort bucketGatewayPort;

    @Override
    public void uploadFile(String bucketName, String directory, String keyName, byte[] fileData) {
        bucketGatewayPort.uploadFileFromBytes(bucketName, directory, keyName, fileData);
    }

    @Override
    public void deleteFile(String bucketName, String directory, String keyName) {
        bucketGatewayPort.deleteFile(bucketName, directory, keyName);
    }
}
