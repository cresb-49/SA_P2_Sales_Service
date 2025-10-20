

package com.sap.sales_service.sale.domain;

import com.sap.common_lib.common.enums.sale.TicketStatusType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class SaleLineTicketTest {

    // Constantes
    private static final UUID ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID SALE_ID = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    private static final int QTY = 2;
    private static final BigDecimal UNIT = new BigDecimal("15.25");
    private static final BigDecimal TOTAL = new BigDecimal("30.50");

    @Test
    void constructor_full_debeAsignarTodosLosCampos() {
        // Arrange
        LocalDateTime createdAt = LocalDateTime.now().minusMinutes(1);
        LocalDateTime updatedAt = LocalDateTime.now().minusSeconds(30);
        // Act
        SaleLineTicket line = new SaleLineTicket(ID, SALE_ID, QTY, UNIT, TOTAL, TicketStatusType.RESERVED, createdAt, updatedAt);
        // Assert
        assertThat(line.getId()).isEqualTo(ID);
        assertThat(line.getSaleId()).isEqualTo(SALE_ID);
        assertThat(line.getQuantity()).isEqualTo(QTY);
        assertThat(line.getUnitPrice()).isEqualTo(UNIT);
        assertThat(line.getTotalPrice()).isEqualTo(TOTAL);
        assertThat(line.getStatus()).isEqualTo(TicketStatusType.RESERVED);
        assertThat(line.getCreatedAt()).isEqualTo(createdAt);
        assertThat(line.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    void constructor_calculaTotal_yValoresPorDefecto() {
        // Arrange
        // Act
        SaleLineTicket line = new SaleLineTicket(SALE_ID, QTY, UNIT);
        // Assert
        assertThat(line.getId()).isNotNull();
        assertThat(line.getSaleId()).isEqualTo(SALE_ID);
        assertThat(line.getQuantity()).isEqualTo(QTY);
        assertThat(line.getUnitPrice()).isEqualTo(UNIT);
        assertThat(line.getTotalPrice()).isEqualByComparingTo(TOTAL);
        assertThat(line.getStatus()).isEqualTo(TicketStatusType.PENDING);
        assertThat(line.getCreatedAt()).isNotNull();
        assertThat(line.getUpdatedAt()).isNotNull();
    }

    @Test
    void setSaleId_debeActualizarElValor() {
        // Arrange
        SaleLineTicket line = new SaleLineTicket(SALE_ID, QTY, UNIT);
        UUID nuevoSaleId = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
        // Act
        line.setSaleId(nuevoSaleId);
        // Assert
        assertThat(line.getSaleId()).isEqualTo(nuevoSaleId);
    }

    @Test
    void validate_debePasar_conDatosValidos() {
        // Arrange
        SaleLineTicket line = new SaleLineTicket(SALE_ID, QTY, UNIT);
        // Act
        line.validate();
        // Assert
        assertThat(line.getTotalPrice()).isEqualByComparingTo(TOTAL);
    }

    @Test
    void validate_debeLanzar_cuandoSaleIdEsNull() {
        // Arrange
        SaleLineTicket line = new SaleLineTicket(ID, null, QTY, UNIT, TOTAL, TicketStatusType.PENDING, LocalDateTime.now(), LocalDateTime.now());
        // Act
        // Assert
        assertThatThrownBy(line::validate).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void validate_debeLanzar_cuandoCantidadNoEsPositiva() {
        // Arrange
        SaleLineTicket zero = new SaleLineTicket(ID, SALE_ID, 0, UNIT, BigDecimal.ZERO, TicketStatusType.PENDING, LocalDateTime.now(), LocalDateTime.now());
        SaleLineTicket negative = new SaleLineTicket(ID, SALE_ID, -1, UNIT, new BigDecimal("-15.25"), TicketStatusType.PENDING, LocalDateTime.now(), LocalDateTime.now());
        // Act
        // Assert
        assertThatThrownBy(zero::validate).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(negative::validate).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void validate_debeLanzar_cuandoPrecioUnitarioNegativo() {
        // Arrange
        SaleLineTicket line = new SaleLineTicket(ID, SALE_ID, QTY, new BigDecimal("-0.01"), new BigDecimal("-0.02"), TicketStatusType.PENDING, LocalDateTime.now(), LocalDateTime.now());
        // Act
        // Assert
        assertThatThrownBy(line::validate).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void validate_debeLanzar_cuandoTotalNegativo() {
        // Arrange
        SaleLineTicket line = new SaleLineTicket(ID, SALE_ID, QTY, UNIT, new BigDecimal("-1"), TicketStatusType.PENDING, LocalDateTime.now(), LocalDateTime.now());
        // Act
        // Assert
        assertThatThrownBy(line::validate).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void use_desdePending_cambiaAInUse() {
        // Arrange
        SaleLineTicket line = new SaleLineTicket(SALE_ID, QTY, UNIT);
        // Act
        line.use();
        // Assert
        assertThat(line.getStatus()).isEqualTo(TicketStatusType.IN_USE);
        assertThat(line.getUpdatedAt()).isNotNull();
    }

    @Test
    void use_desdeNoPending_lanzaExcepcion() {
        // Arrange
        SaleLineTicket line = new SaleLineTicket(ID, SALE_ID, QTY, UNIT, TOTAL, TicketStatusType.RESERVED, LocalDateTime.now(), LocalDateTime.now());
        // Act
        // Assert
        assertThatThrownBy(line::use).isInstanceOf(RuntimeException.class);
    }

    @Test
    void cancel_desdePending_cambiaACancelled() {
        // Arrange
        SaleLineTicket line = new SaleLineTicket(SALE_ID, QTY, UNIT);
        // Act
        line.cancel();
        // Assert
        assertThat(line.getStatus()).isEqualTo(TicketStatusType.CANCELLED);
    }

    @Test
    void cancel_desdeReserved_cambiaACancelled() {
        // Arrange
        SaleLineTicket line = new SaleLineTicket(ID, SALE_ID, QTY, UNIT, TOTAL, TicketStatusType.RESERVED, LocalDateTime.now(), LocalDateTime.now());
        // Act
        line.cancel();
        // Assert
        assertThat(line.getStatus()).isEqualTo(TicketStatusType.CANCELLED);
    }

    @Test
    void cancel_desdeEstadoNoPermitido_lanzaExcepcion() {
        // Arrange
        SaleLineTicket line = new SaleLineTicket(ID, SALE_ID, QTY, UNIT, TOTAL, TicketStatusType.IN_USE, LocalDateTime.now(), LocalDateTime.now());
        // Act
        // Assert
        assertThatThrownBy(line::cancel).isInstanceOf(RuntimeException.class);
    }

    @Test
    void purchase_desdeReserved_cambiaAPurchased() {
        // Arrange
        SaleLineTicket line = new SaleLineTicket(ID, SALE_ID, QTY, UNIT, TOTAL, TicketStatusType.RESERVED, LocalDateTime.now(), LocalDateTime.now());
        // Act
        line.purchase();
        // Assert
        assertThat(line.getStatus()).isEqualTo(TicketStatusType.PURCHASED);
    }

    @Test
    void purchase_desdeNoReserved_lanzaExcepcion() {
        // Arrange
        SaleLineTicket line = new SaleLineTicket(SALE_ID, QTY, UNIT); // PENDING
        // Act
        // Assert
        assertThatThrownBy(line::purchase).isInstanceOf(RuntimeException.class);
    }

    @Test
    void reserve_desdePending_cambiaAReserved() {
        // Arrange
        SaleLineTicket line = new SaleLineTicket(SALE_ID, QTY, UNIT); // PENDING
        // Act
        line.reserve();
        // Assert
        assertThat(line.getStatus()).isEqualTo(TicketStatusType.RESERVED);
    }

    @Test
    void reserve_desdeNoPending_lanzaExcepcion() {
        // Arrange
        SaleLineTicket line = new SaleLineTicket(ID, SALE_ID, QTY, UNIT, TOTAL, TicketStatusType.RESERVED, LocalDateTime.now(), LocalDateTime.now());
        // Act
        // Assert
        assertThatThrownBy(line::reserve).isInstanceOf(RuntimeException.class);
    }

    @Test
    void pend_desdeReserved_cambiaAPending() {
        // Arrange
        SaleLineTicket line = new SaleLineTicket(ID, SALE_ID, QTY, UNIT, TOTAL, TicketStatusType.RESERVED, LocalDateTime.now(), LocalDateTime.now());
        // Act
        line.pend();
        // Assert
        assertThat(line.getStatus()).isEqualTo(TicketStatusType.PENDING);
    }

    @Test
    void pend_desdeNoReserved_lanzaExcepcion() {
        // Arrange
        SaleLineTicket line = new SaleLineTicket(SALE_ID, QTY, UNIT); // PENDING
        // Act
        // Assert
        assertThatThrownBy(line::pend).isInstanceOf(RuntimeException.class);
    }
}