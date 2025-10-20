

package com.sap.sales_service.snacks.application.usecases.createsnack;

import com.sap.sales_service.snacks.application.ouput.FindingSnackPort;
import com.sap.sales_service.snacks.application.ouput.SaveFilePort;
import com.sap.sales_service.snacks.application.ouput.SaveSnackPort;
import com.sap.sales_service.snacks.application.usecases.createsnack.dtos.CreateSnackDTO;
import com.sap.sales_service.snacks.domain.Snack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class CreateSnackCaseTest {

    private static final UUID CINEMA_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final String NAME = "Popcorn";
    private static final BigDecimal PRICE = new BigDecimal("12.50");
    private static final String BUCKET = "bucket-test";
    private static final String DIR = "snacks";
    private static final String REGION = "us-east-1";
    private static final String EXTERNAL_URL = "https://example.com/image.png";

    @Mock
    private SaveSnackPort saveSnackPort;
    @Mock
    private FindingSnackPort findingSnackPort;
    @Mock
    private SaveFilePort saveFilePort;
    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private CreateSnackCase useCase;

    @BeforeEach
    void init() throws Exception {
        MockitoAnnotations.openMocks(this);
        setField(useCase, "bucketName", BUCKET);
        setField(useCase, "bucketDirectory", DIR);
        setField(useCase, "awsRegion", REGION);
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }

    @Test
    void create_shouldSucceed_withExternalUrl_onlyUrlProvided() {
        // Arrange
        var dto = new CreateSnackDTO(CINEMA_ID, NAME, EXTERNAL_URL, PRICE, null);
        given(findingSnackPort.findLikeNameAndCinemaId(NAME, CINEMA_ID)).willReturn(Optional.empty());
        ArgumentCaptor<Snack> snackCaptor = ArgumentCaptor.forClass(Snack.class);
        given(saveSnackPort.save(any(Snack.class))).willAnswer(inv -> inv.getArgument(0));

        // Act
        var result = useCase.create(dto);

        // Assert
        verify(saveFilePort, never()).uploadFile(anyString(), anyString(), anyString(), any());
        verify(saveSnackPort).save(snackCaptor.capture());
        var saved = snackCaptor.getValue();
        assertThat(saved.getCinemaId()).isEqualTo(CINEMA_ID);
        assertThat(saved.getName()).isEqualTo(NAME);
        assertThat(saved.getPrice()).isEqualByComparingTo(PRICE);
        assertThat(saved.isExternalImage()).isTrue();
        assertThat(saved.getImageUrl()).isEqualTo(EXTERNAL_URL);
        assertThat(result.getId()).isNotNull();
    }

    @Test
    void create_shouldSucceed_withLocalFile_onlyFileProvided() throws Exception {
        // Arrange
        var dto = new CreateSnackDTO(CINEMA_ID, NAME, null, PRICE, multipartFile);
        given(findingSnackPort.findLikeNameAndCinemaId(NAME, CINEMA_ID)).willReturn(Optional.empty());
        given(multipartFile.isEmpty()).willReturn(false);
        given(multipartFile.getOriginalFilename()).willReturn("photo.png");
        given(multipartFile.getBytes()).willReturn(new byte[]{1, 2, 3});
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Snack> snackCaptor = ArgumentCaptor.forClass(Snack.class);
        given(saveSnackPort.save(any(Snack.class))).willAnswer(inv -> inv.getArgument(0));

        // Act
        var result = useCase.create(dto);

        // Assert
        verify(saveFilePort).uploadFile(eq(BUCKET), eq(DIR), keyCaptor.capture(), eq(new byte[]{1,2,3}));
        String generatedKey = keyCaptor.getValue();
        assertThat(generatedKey).startsWith("snack_").endsWith(".png");
        verify(saveSnackPort).save(snackCaptor.capture());
        var saved = snackCaptor.getValue();
        assertThat(saved.getCinemaId()).isEqualTo(CINEMA_ID);
        assertThat(saved.getName()).isEqualTo(NAME);
        assertThat(saved.getPrice()).isEqualByComparingTo(PRICE);
        assertThat(saved.isExternalImage()).isFalse();
        assertThat(saved.getImageUrl()).contains("https://" + BUCKET + ".s3." + REGION + ".amazonaws.com/" + DIR + "/snack_");
        assertThat(saved.getImageUrl()).endsWith(".png");
        assertThat(result.getId()).isNotNull();
    }

    @Test
    void create_shouldThrow_whenSnackAlreadyExists_sameCinemaAndName() {
        // Arrange
        var dto = new CreateSnackDTO(CINEMA_ID, NAME, EXTERNAL_URL, PRICE, null);
        given(findingSnackPort.findLikeNameAndCinemaId(NAME, CINEMA_ID))
                .willReturn(Optional.of(new Snack(CINEMA_ID, NAME, PRICE, true, EXTERNAL_URL)));

        // Act & Assert
        assertThatThrownBy(() -> useCase.create(dto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void create_shouldThrow_whenNoFileAndNoUrlProvided() {
        // Arrange
        var dto = new CreateSnackDTO(CINEMA_ID, NAME, null, PRICE, null);
        given(findingSnackPort.findLikeNameAndCinemaId(NAME, CINEMA_ID)).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> useCase.create(dto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void create_shouldThrow_whenInvalidExtension_andNoExternalUrl() throws Exception {
        // Arrange
        var dto = new CreateSnackDTO(CINEMA_ID, NAME, null, PRICE, multipartFile);
        given(findingSnackPort.findLikeNameAndCinemaId(NAME, CINEMA_ID)).willReturn(Optional.empty());
        given(multipartFile.isEmpty()).willReturn(false);
        given(multipartFile.getOriginalFilename()).willReturn("photo.bmp");

        // Act & Assert
        assertThatThrownBy(() -> useCase.create(dto))
                .isInstanceOf(IllegalArgumentException.class);
        verify(saveFilePort, never()).uploadFile(anyString(), anyString(), anyString(), any());
        verify(saveSnackPort, never()).save(any());
    }

    @Test
    void create_shouldPreferExternalUrl_whenBothUrlAndFileProvided() throws Exception {
        // Arrange
        var dto = new CreateSnackDTO(CINEMA_ID, NAME, EXTERNAL_URL, PRICE, multipartFile);
        given(findingSnackPort.findLikeNameAndCinemaId(NAME, CINEMA_ID)).willReturn(Optional.empty());
        given(multipartFile.isEmpty()).willReturn(false);
        given(multipartFile.getOriginalFilename()).willReturn("img.gif");
        given(multipartFile.getBytes()).willReturn(new byte[]{9});
        ArgumentCaptor<Snack> snackCaptor = ArgumentCaptor.forClass(Snack.class);
        given(saveSnackPort.save(any(Snack.class))).willAnswer(inv -> inv.getArgument(0));

        // Act
        var result = useCase.create(dto);

        // Assert
        verify(saveFilePort, never()).uploadFile(anyString(), anyString(), anyString(), any());
        verify(saveSnackPort).save(snackCaptor.capture());
        var saved = snackCaptor.getValue();
        assertThat(saved.isExternalImage()).isTrue();
        assertThat(saved.getImageUrl()).isEqualTo(EXTERNAL_URL);
        assertThat(result.getId()).isNotNull();
    }
}