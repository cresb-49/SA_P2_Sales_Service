

package com.sap.sales_service.s3.infrastructure.output.s3;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import java.util.function.Consumer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.nio.file.Files;
import java.nio.file.Path;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private S3Service s3Service;

    private static final String BUCKET = "bucket-test";
    private static final String DIR = "images";
    private static final String KEY = "photo.jpg";
    private static final byte[] DATA = {1, 2, 3};

    @Test
    @DisplayName("uploadFileFromBytes debe construir PutObjectRequest y delegar en S3Client")
    void uploadFileFromBytes_shouldDelegateToS3Client() {
        // Arrange
        // Act
        s3Service.uploadFileFromBytes(BUCKET, DIR, KEY, DATA);
        // Assert
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("uploadFileFromFile debe construir PutObjectRequest y delegar en S3Client")
    void uploadFileFromFile_shouldDelegateToS3Client() throws IOException {
        // Arrange
        Path tmp = Files.createTempFile("dummy", ".txt");
        Files.write(tmp, DATA);
        File tmpFile = tmp.toFile();
        // Act
        s3Service.uploadFileFromFile(BUCKET, DIR, KEY, tmpFile);
        // Assert
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        Files.deleteIfExists(tmp);
    }

    @Test
    @DisplayName("downloadFile debe delegar en S3Client y devolver bytes leídos")
    void downloadFile_shouldReturnBytes() throws IOException {
        // Arrange
        ResponseInputStream<GetObjectResponse> responseStream = new ResponseInputStream<>(
                GetObjectResponse.builder().build(),
                software.amazon.awssdk.http.AbortableInputStream.create(new ByteArrayInputStream(DATA))
        );
        when(s3Client.getObject(any(Consumer.class))).thenReturn(responseStream);
        // Act
        byte[] result = s3Service.downloadFile(BUCKET, DIR, KEY);
        // Assert
        verify(s3Client, times(1)).getObject(any(Consumer.class));
        assertThat(result).isEqualTo(DATA);
    }

    @Test
    @DisplayName("deleteFile debe construir DeleteObjectRequest y delegar en S3Client")
    void deleteFile_shouldDelegateToS3Client() {
        // Arrange
        // Act
        s3Service.deleteFile(BUCKET, DIR, KEY);
        // Assert
        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
    }
}