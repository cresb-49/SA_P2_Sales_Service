package com.sap.sales_service.snacks.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class SnackTest {

    private static final String NAME = "Popcorn";
    private static final BigDecimal PRICE = BigDecimal.valueOf(12.50);
    private static final boolean EXTERNAL_IMAGE = true;
    private static final String IMAGE_URL = "https://cdn/img.png";

    private UUID cinemaId;

    @BeforeEach
    void setUp() {
        cinemaId = UUID.randomUUID();
    }

    @Test
    void constructor_shouldInitializeFields_andSetActiveTrue() {
        // Arrange
        // Act
        var snack = new Snack(cinemaId, NAME, PRICE, EXTERNAL_IMAGE, IMAGE_URL);
        // Assert
        assertThat(snack.getId()).isNotNull();
        assertThat(snack.getCinemaId()).isEqualTo(cinemaId);
        assertThat(snack.getName()).isEqualTo(NAME);
        assertThat(snack.getPrice()).isEqualByComparingTo(PRICE);
        assertThat(snack.isExternalImage()).isTrue();
        assertThat(snack.getImageUrl()).isEqualTo(IMAGE_URL);
        assertThat(snack.isActive()).isTrue();
        assertThat(snack.getCreatedAt()).isNotNull();
        assertThat(snack.getUpdatedAt()).isNotNull();
    }

    @Test
    void update_shouldChangeNamePriceImage_andRefreshUpdatedAt_whenValuesProvided() throws InterruptedException {
        // Arrange
        var snack = new Snack(cinemaId, NAME, PRICE, EXTERNAL_IMAGE, IMAGE_URL);
        LocalDateTime before = snack.getUpdatedAt();
        Thread.sleep(5);
        var newName = "Nachos";
        var newPrice = BigDecimal.valueOf(15.75);
        var newImageUrl = "https://cdn/new.png";
        // Act
        snack.update(newName, newPrice, false, newImageUrl);
        // Assert
        assertThat(snack.getName()).isEqualTo(newName);
        assertThat(snack.getPrice()).isEqualByComparingTo(newPrice);
        assertThat(snack.isExternalImage()).isFalse();
        assertThat(snack.getImageUrl()).isEqualTo(newImageUrl);
        assertThat(snack.getUpdatedAt()).isAfter(before);
    }

    @Test
    void update_shouldNotRefreshUpdatedAt_whenOnlyExternalImageFlagChanges() throws InterruptedException {
        // Arrange
        var snack = new Snack(cinemaId, NAME, PRICE, EXTERNAL_IMAGE, IMAGE_URL);
        LocalDateTime before = snack.getUpdatedAt();
        Thread.sleep(5);
        // Act
        snack.update("", null, false, "");
        // Assert
        assertThat(snack.isExternalImage()).isFalse();
        assertThat(snack.getName()).isEqualTo(NAME);
        assertThat(snack.getPrice()).isEqualByComparingTo(PRICE);
        assertThat(snack.getImageUrl()).isEqualTo(IMAGE_URL);
        assertThat(snack.getUpdatedAt()).isEqualTo(before);
    }

    @Test
    void update_shouldIgnoreNullsAndEmpties_forNamePriceAndImage() {
        // Arrange
        var snack = new Snack(cinemaId, NAME, PRICE, EXTERNAL_IMAGE, IMAGE_URL);
        var originalUpdatedAt = snack.getUpdatedAt();
        // Act
        snack.update(null, null, true, null);
        // Assert
        assertThat(snack.getName()).isEqualTo(NAME);
        assertThat(snack.getPrice()).isEqualByComparingTo(PRICE);
        assertThat(snack.getImageUrl()).isEqualTo(IMAGE_URL);
        assertThat(snack.isExternalImage()).isTrue();
        assertThat(snack.getUpdatedAt()).isEqualTo(originalUpdatedAt);
    }

    @Test
    void toggleActive_shouldFlipState_andRefreshUpdatedAt() throws InterruptedException {
        // Arrange
        var snack = new Snack(cinemaId, NAME, PRICE, EXTERNAL_IMAGE, IMAGE_URL);
        var before = snack.getUpdatedAt();
        Thread.sleep(5);
        var initialActive = snack.isActive();
        // Act
        snack.toggleActive();
        // Assert
        assertThat(snack.isActive()).isEqualTo(!initialActive);
        assertThat(snack.getUpdatedAt()).isAfter(before);
    }

    @Test
    void validate_shouldPass_withValidData() {
        // Arrange
        var snack = new Snack(cinemaId, NAME, PRICE, EXTERNAL_IMAGE, IMAGE_URL);
        // Act
        // Assert
        assertThatCode(snack::validate).doesNotThrowAnyException();
    }

    @Test
    void validate_shouldThrow_whenNameNull() {
        // Arrange
        var snack = new Snack(cinemaId, null, PRICE, EXTERNAL_IMAGE, IMAGE_URL);
        // Act
        // Assert
        assertThatThrownBy(snack::validate).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void validate_shouldThrow_whenNameEmpty() {
        // Arrange
        var snack = new Snack(cinemaId, "", PRICE, EXTERNAL_IMAGE, IMAGE_URL);
        // Act
        // Assert
        assertThatThrownBy(snack::validate).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void validate_shouldThrow_whenPriceNull() {
        // Arrange
        var snack = new Snack(cinemaId, NAME, null, EXTERNAL_IMAGE, IMAGE_URL);
        // Act
        // Assert
        assertThatThrownBy(snack::validate).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void validate_shouldThrow_whenPriceNegative() {
        // Arrange
        var snack = new Snack(cinemaId, NAME, BigDecimal.valueOf(-1), EXTERNAL_IMAGE, IMAGE_URL);
        // Act
        // Assert
        assertThatThrownBy(snack::validate).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void validate_shouldThrow_whenImageUrlNull() {
        // Arrange
        var snack = new Snack(cinemaId, NAME, PRICE, EXTERNAL_IMAGE, null);
        // Act
        // Assert
        assertThatThrownBy(snack::validate).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void validate_shouldThrow_whenImageUrlEmpty() {
        // Arrange
        var snack = new Snack(cinemaId, NAME, PRICE, EXTERNAL_IMAGE, "");
        // Act
        // Assert
        assertThatThrownBy(snack::validate).isInstanceOf(IllegalArgumentException.class);
    }
}