

package com.sap.sales_service.snacks.infrastructure.output.bucket;

import com.sap.sales_service.s3.infrastructure.input.port.BucketGatewayPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BucketAdapterTest {

    private static final String BUCKET = "bucket";
    private static final String DIRECTORY = "dir";
    private static final String KEY = "file.png";
    private static final byte[] DATA = new byte[]{1, 2, 3};

    @Mock
    private BucketGatewayPort bucketGatewayPort;

    @InjectMocks
    private BucketAdapter bucketAdapter;

    @Test
    void uploadFile_shouldDelegateToGateway() {
        // Arrange
        // Act
        bucketAdapter.uploadFile(BUCKET, DIRECTORY, KEY, DATA);
        // Assert
        verify(bucketGatewayPort).uploadFileFromBytes(BUCKET, DIRECTORY, KEY, DATA);
        verifyNoMoreInteractions(bucketGatewayPort);
    }

    @Test
    void deleteFile_shouldDelegateToGateway() {
        // Arrange
        // Act
        bucketAdapter.deleteFile(BUCKET, DIRECTORY, KEY);
        // Assert
        verify(bucketGatewayPort).deleteFile(BUCKET, DIRECTORY, KEY);
        verifyNoMoreInteractions(bucketGatewayPort);
    }
}