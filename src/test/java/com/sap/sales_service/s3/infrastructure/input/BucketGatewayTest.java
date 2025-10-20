package com.sap.sales_service.s3.infrastructure.input;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sap.sales_service.s3.infrastructure.output.adapter.S3ServicePort;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BucketGatewayTest {

    @Mock
    private S3ServicePort s3ServicePort;

    @InjectMocks
    private BucketGateway bucketGateway;

    private static final String BUCKET = "bucket-test";
    private static final String DIRECTORY = "dir";
    private static final String KEY = "file.txt";
    private static final byte[] DATA = {1, 2, 3};
    private static final File FILE = new File("dummy.txt");

    @Test
    @DisplayName("uploadFileFromBytes debe delegar en s3ServicePort")
    void uploadFileFromBytes_shouldDelegate() {
        // Arrange
        // Act
        bucketGateway.uploadFileFromBytes(BUCKET, DIRECTORY, KEY, DATA);
        // Assert
        verify(s3ServicePort, times(1)).uploadFileFromBytes(BUCKET, DIRECTORY, KEY, DATA);
    }

    @Test
    @DisplayName("uploadFileFromFile debe delegar en s3ServicePort")
    void uploadFileFromFile_shouldDelegate() {
        // Arrange
        // Act
        bucketGateway.uploadFileFromFile(BUCKET, DIRECTORY, KEY, FILE);
        // Assert
        verify(s3ServicePort, times(1)).uploadFileFromFile(BUCKET, DIRECTORY, KEY, FILE);
    }

    @Test
    @DisplayName("downloadFile debe delegar en s3ServicePort y retornar bytes")
    void downloadFile_shouldDelegateAndReturnBytes() throws IOException {
        // Arrange
        when(s3ServicePort.downloadFile(BUCKET, DIRECTORY, KEY)).thenReturn(DATA);
        // Act
        byte[] result = bucketGateway.downloadFile(BUCKET, DIRECTORY, KEY);
        // Assert
        verify(s3ServicePort, times(1)).downloadFile(BUCKET, DIRECTORY, KEY);
        assertThat(result).isEqualTo(DATA);
    }

    @Test
    @DisplayName("deleteFile debe delegar en s3ServicePort")
    void deleteFile_shouldDelegate() {
        // Arrange
        // Act
        bucketGateway.deleteFile(BUCKET, DIRECTORY, KEY);
        // Assert
        verify(s3ServicePort, times(1)).deleteFile(BUCKET, DIRECTORY, KEY);
    }
}