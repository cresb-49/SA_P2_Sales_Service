
package com.sap.sales_service.snacks.application.usecases.updatesnack;

import com.sap.common_lib.exception.NotFoundException;
import com.sap.sales_service.snacks.application.ouput.DeletingFilePort;
import com.sap.sales_service.snacks.application.ouput.FindingSnackPort;
import com.sap.sales_service.snacks.application.ouput.SaveFilePort;
import com.sap.sales_service.snacks.application.ouput.SaveSnackPort;
import com.sap.sales_service.snacks.application.usecases.updatesnack.dtos.UpdateSnackDTO;
import com.sap.sales_service.snacks.domain.Snack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateSnackCaseTest {

    private static final UUID ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID CINEMA_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final String NAME = "Popcorn";
    private static final String NEW_NAME = "Caramel Popcorn";
    private static final BigDecimal PRICE = new BigDecimal("10.50");
    private static final BigDecimal NEW_PRICE = new BigDecimal("12.00");
    private static final String BUCKET = "bucket";
    private static final String DIR = "dir";
    private static final String REGION = "us-east-1";
    private static final String OLD_LOCAL_URL = "https://" + BUCKET + ".s3." + REGION + ".amazonaws.com/" + DIR
            + "/snack_old.png";
    private static final String OLD_EXTERNAL_URL = "https://images.example.com/snack_old.png";
    private static final String NEW_EXTERNAL_URL = "https://cdn.example.com/new.png";

    @Mock
    private FindingSnackPort findingSnackPort;
    @Mock
    private SaveFilePort saveFilePort;
    @Mock
    private DeletingFilePort deletingFilePort;
    @Mock
    private SaveSnackPort saveSnackPort;
    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private UpdateSnackCase useCase;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(useCase, "bucketName", BUCKET);
        ReflectionTestUtils.setField(useCase, "bucketDirectory", DIR);
        ReflectionTestUtils.setField(useCase, "awsRegion", REGION);
    }

    @Test
    void update_shouldModifyOnlyFields_whenNoFileAndNoUrl() throws Exception {
        // Arrange
        Snack existing = new Snack(CINEMA_ID, NAME, PRICE, true, OLD_EXTERNAL_URL);
        ReflectionTestUtils.setField(existing, "id", ID);
        given(findingSnackPort.findById(ID)).willReturn(Optional.of(existing));
        given(saveSnackPort.save(any(Snack.class))).willAnswer(inv -> inv.getArgument(0));
        UpdateSnackDTO dto = new UpdateSnackDTO(ID, NEW_NAME, NEW_PRICE, null, null);

        // Act
        Snack result = useCase.update(dto);

        // Assert
        assertNotNull(result);
        verify(saveFilePort, never()).uploadFile(anyString(), anyString(), anyString(), any());
        verify(deletingFilePort, never()).deleteFile(anyString(), anyString(), anyString());
        verify(saveSnackPort).save(any(Snack.class));
    }

    @Test
    void update_shouldSwitchToExternalUrl_andDeleteOldLocal() throws Exception {
        // Arrange
        Snack existing = new Snack(CINEMA_ID, NAME, PRICE, false, OLD_LOCAL_URL);
        ReflectionTestUtils.setField(existing, "id", ID);
        given(findingSnackPort.findById(ID)).willReturn(Optional.of(existing));
        given(saveSnackPort.save(any(Snack.class))).willAnswer(inv -> inv.getArgument(0));
        UpdateSnackDTO dto = new UpdateSnackDTO(ID, null, null, NEW_EXTERNAL_URL, null);

        // Act
        Snack result = useCase.update(dto);

        // Assert
        assertNotNull(result);
        verify(saveFilePort, never()).uploadFile(anyString(), anyString(), anyString(), any());
        verify(deletingFilePort).deleteFile(eq(BUCKET), eq(DIR), eq("snack_old.png"));
        verify(saveSnackPort).save(any(Snack.class));
    }

    @Test
    void update_shouldSwitchToExternalUrl_withoutDeleting_whenOldWasExternal() throws Exception {
        // Arrange
        Snack existing = new Snack(CINEMA_ID, NAME, PRICE, true, OLD_EXTERNAL_URL);
        ReflectionTestUtils.setField(existing, "id", ID);
        given(findingSnackPort.findById(ID)).willReturn(Optional.of(existing));
        given(saveSnackPort.save(any(Snack.class))).willAnswer(inv -> inv.getArgument(0));
        UpdateSnackDTO dto = new UpdateSnackDTO(ID, null, null, NEW_EXTERNAL_URL, null);

        // Act
        Snack result = useCase.update(dto);

        // Assert
        assertNotNull(result);
        verify(saveFilePort, never()).uploadFile(anyString(), anyString(), anyString(), any());
        verify(deletingFilePort, never()).deleteFile(anyString(), anyString(), anyString());
        verify(saveSnackPort).save(any(Snack.class));
    }

    @Test
    void update_shouldUploadNewFile_andDeleteOldLocal() throws Exception {
        // Arrange
        Snack existing = new Snack(CINEMA_ID, NAME, PRICE, false, OLD_LOCAL_URL);
        ReflectionTestUtils.setField(existing, "id", ID);
        given(findingSnackPort.findById(ID)).willReturn(Optional.of(existing));
        given(saveSnackPort.save(any(Snack.class))).willAnswer(inv -> inv.getArgument(0));
        given(multipartFile.isEmpty()).willReturn(false);
        given(multipartFile.getOriginalFilename()).willReturn("image.png");
        given(multipartFile.getBytes()).willReturn(new byte[] { 1, 2, 3 });
        UpdateSnackDTO dto = new UpdateSnackDTO(ID, null, null, null, multipartFile);

        // Act
        Snack result = useCase.update(dto);

        // Assert
        assertNotNull(result);
        verify(saveFilePort).uploadFile(eq(BUCKET), eq(DIR), argThat(nameEndsWithPngAndStartsWithSnack()), any());
        verify(deletingFilePort).deleteFile(eq(BUCKET), eq(DIR), eq("snack_old.png"));
        verify(saveSnackPort).save(any(Snack.class));
    }

    @Test
    void update_shouldUploadNewFile_withoutDeleting_whenOldWasExternal() throws Exception {
        // Arrange
        Snack existing = new Snack(CINEMA_ID, NAME, PRICE, true, OLD_EXTERNAL_URL);
        ReflectionTestUtils.setField(existing, "id", ID);
        given(findingSnackPort.findById(ID)).willReturn(Optional.of(existing));
        given(saveSnackPort.save(any(Snack.class))).willAnswer(inv -> inv.getArgument(0));
        given(multipartFile.isEmpty()).willReturn(false);
        given(multipartFile.getOriginalFilename()).willReturn("image.png");
        given(multipartFile.getBytes()).willReturn(new byte[] { 1, 2, 3 });
        UpdateSnackDTO dto = new UpdateSnackDTO(ID, null, null, null, multipartFile);

        // Act
        Snack result = useCase.update(dto);

        // Assert
        assertNotNull(result);
        verify(saveFilePort).uploadFile(eq(BUCKET), eq(DIR), argThat(nameEndsWithPngAndStartsWithSnack()), any());
        verify(deletingFilePort, never()).deleteFile(anyString(), anyString(), anyString());
        verify(saveSnackPort).save(any(Snack.class));
    }

    @Test
    void update_shouldThrow_whenInvalidExtension() {
        // Arrange
        given(multipartFile.isEmpty()).willReturn(false);
        given(multipartFile.getOriginalFilename()).willReturn("file.txt");
        UpdateSnackDTO dto = new UpdateSnackDTO(ID, null, null, null, multipartFile);

        // Act / Assert
        assertThatThrownBy(() -> useCase.update(dto))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(findingSnackPort, saveSnackPort, saveFilePort, deletingFilePort);
    }

    @Test
    void update_shouldThrow_whenSnackNotFound() {
        // Arrange
        given(findingSnackPort.findById(ID)).willReturn(Optional.empty());
        UpdateSnackDTO dto = new UpdateSnackDTO(ID, null, null, null, null);

        // Act / Assert
        assertThatThrownBy(() -> useCase.update(dto))
                .isInstanceOf(NotFoundException.class);
        verify(saveFilePort, never()).uploadFile(anyString(), anyString(), anyString(), any());
        verify(deletingFilePort, never()).deleteFile(anyString(), anyString(), anyString());
        verify(saveSnackPort, never()).save(any(Snack.class));
    }

    private ArgumentMatcher<String> nameEndsWithPngAndStartsWithSnack() {
        return s -> s != null && s.startsWith("snack_") && s.endsWith(".png");
    }
}