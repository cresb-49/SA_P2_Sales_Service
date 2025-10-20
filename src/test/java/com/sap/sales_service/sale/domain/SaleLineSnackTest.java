

package com.sap.sales_service.sale.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SaleLineSnackTest {

    // Constantes
    private static final UUID ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID SALE_ID = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    private static final UUID SNACK_ID = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
    private static final int QUANTITY = 3;
    private static final BigDecimal UNIT_PRICE = new BigDecimal("12.50");
    private static final BigDecimal TOTAL_PRICE = new BigDecimal("37.50"); // 12.50 * 3

    @Test
    void constructor_full_shouldSetAllFields() {
        // Arrange
        // Act
        SaleLineSnack line = new SaleLineSnack(ID, SALE_ID, SNACK_ID, QUANTITY, UNIT_PRICE, TOTAL_PRICE);
        // Assert
        assertThat(line.getId()).isEqualTo(ID);
        assertThat(line.getSaleId()).isEqualTo(SALE_ID);
        assertThat(line.getSnackId()).isEqualTo(SNACK_ID);
        assertThat(line.getQuantity()).isEqualTo(QUANTITY);
        assertThat(line.getUnitPrice()).isEqualTo(UNIT_PRICE);
        assertThat(line.getTotalPrice()).isEqualTo(TOTAL_PRICE);
    }

    @Test
    void constructor_calcTotal_shouldComputeTotalPrice() {
        // Arrange
        // Act
        SaleLineSnack line = new SaleLineSnack(SALE_ID, SNACK_ID, QUANTITY, UNIT_PRICE);
        // Assert
        assertThat(line.getSaleId()).isEqualTo(SALE_ID);
        assertThat(line.getSnackId()).isEqualTo(SNACK_ID);
        assertThat(line.getQuantity()).isEqualTo(QUANTITY);
        assertThat(line.getUnitPrice()).isEqualTo(UNIT_PRICE);
        assertThat(line.getTotalPrice()).isEqualByComparingTo(TOTAL_PRICE);
    }

    @Test
    void setSaleId_shouldUpdateValue() {
        // Arrange
        SaleLineSnack line = new SaleLineSnack(SALE_ID, SNACK_ID, QUANTITY, UNIT_PRICE);
        UUID newSaleId = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");
        // Act
        line.setSaleId(newSaleId);
        // Assert
        assertThat(line.getSaleId()).isEqualTo(newSaleId);
    }

    @Test
    void validate_shouldPass_withValidData() {
        // Arrange
        SaleLineSnack line = new SaleLineSnack(SALE_ID, SNACK_ID, QUANTITY, UNIT_PRICE);
        // Act
        line.validate();
        // Assert
        assertThat(line.getTotalPrice()).isEqualByComparingTo(TOTAL_PRICE);
    }

    @Test
    void validate_shouldThrow_whenSaleIdIsNull() {
        // Arrange
        SaleLineSnack line = new SaleLineSnack(ID, null, SNACK_ID, QUANTITY, UNIT_PRICE, TOTAL_PRICE);
        // Act
        // Assert
        assertThatThrownBy(line::validate)
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void validate_shouldThrow_whenQuantityIsZeroOrNegative() {
        // Arrange
        SaleLineSnack zeroQty = new SaleLineSnack(ID, SALE_ID, SNACK_ID, 0, UNIT_PRICE, BigDecimal.ZERO);
        SaleLineSnack negativeQty = new SaleLineSnack(ID, SALE_ID, SNACK_ID, -1, UNIT_PRICE, new BigDecimal("-12.50"));
        // Act
        // Assert
        assertThatThrownBy(zeroQty::validate)
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(negativeQty::validate)
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void validate_shouldThrow_whenUnitPriceNegative() {
        // Arrange
        BigDecimal negativeUnit = new BigDecimal("-0.01");
        SaleLineSnack line = new SaleLineSnack(ID, SALE_ID, SNACK_ID, QUANTITY, negativeUnit, new BigDecimal("-0.03"));
        // Act
        // Assert
        assertThatThrownBy(line::validate)
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void validate_shouldThrow_whenTotalPriceNegative() {
        // Arrange
        SaleLineSnack line = new SaleLineSnack(ID, SALE_ID, SNACK_ID, QUANTITY, UNIT_PRICE, new BigDecimal("-1"));
        // Act
        // Assert
        assertThatThrownBy(line::validate)
                .isInstanceOf(IllegalArgumentException.class);
    }
}