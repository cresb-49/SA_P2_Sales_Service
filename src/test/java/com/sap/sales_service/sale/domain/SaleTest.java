

package com.sap.sales_service.sale.domain;

import com.sap.common_lib.common.enums.sale.SaleStatusType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class SaleTest {

    // Constantes
    private static final UUID ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID CLIENT_ID = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    private static final UUID CINEMA_ID = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
    private static final BigDecimal UNIT_SNACK = new BigDecimal("10.00");
    private static final BigDecimal UNIT_TICKET = new BigDecimal("25.00");

    private SaleLineSnack snackLine(int qty, BigDecimal unit) {
        return new SaleLineSnack(UUID.randomUUID(), null, UUID.randomUUID(), qty, unit, unit.multiply(BigDecimal.valueOf(qty)));
    }

    private SaleLineTicket ticketLine(int qty, BigDecimal unit) {
        return new SaleLineTicket(null, qty, unit);
    }

    private Sale buildSaleWithStatus(SaleStatusType status) {
        LocalDateTime now = LocalDateTime.now();
        Sale sale = new Sale(
                ID,
                CLIENT_ID,
                CINEMA_ID,
                new BigDecimal("100.00"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                status,
                now.minusMinutes(1),
                now.minusSeconds(30),
                null
        );
        sale.setSaleLineSnacks(List.of());
        sale.setSaleLineTickets(List.of());
        return sale;
    }

    @Test
    void constructor_nuevoSale_calculaTotales_yAsignaIdsALineas() {
        // Arrange
        var snacks = List.of(
                snackLine(2, UNIT_SNACK)
        );
        var tickets = List.of(
                ticketLine(1, UNIT_TICKET)
        );
        BigDecimal descuento = new BigDecimal("5.00");
        // Act
        Sale sale = new Sale(CLIENT_ID, CINEMA_ID, descuento, snacks, tickets);
        // Assert
        BigDecimal esperado = UNIT_SNACK.multiply(BigDecimal.valueOf(2)).add(UNIT_TICKET);
        assertThat(sale.getTotalAmount()).isEqualByComparingTo(esperado);
        assertThat(sale.getClaimedAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(sale.getDiscountedAmount()).isEqualByComparingTo(descuento);
        assertThat(sale.getStatus()).isEqualTo(SaleStatusType.PENDING);
        assertThat(sale.getCreatedAt()).isNotNull();
        assertThat(sale.getUpdatedAt()).isNotNull();
        assertThat(sale.getPaidAt()).isNull();
        assertThat(sale.getSaleLineSnacks()).allSatisfy(l -> assertThat(l.getSaleId()).isEqualTo(sale.getId()));
        assertThat(sale.getSaleLineTickets()).allSatisfy(l -> assertThat(l.getSaleId()).isEqualTo(sale.getId()));
    }

    @Test
    void copyConstructor_debeCopiarCamposBasicos() {
        // Arrange
        Sale original = buildSaleWithStatus(SaleStatusType.PENDING);
        // Act
        Sale copia = new Sale(original);
        // Assert
        assertThat(copia.getId()).isEqualTo(original.getId());
        assertThat(copia.getClientId()).isEqualTo(original.getClientId());
        assertThat(copia.getCinemaId()).isEqualTo(original.getCinemaId());
        assertThat(copia.getTotalAmount()).isEqualByComparingTo(original.getTotalAmount());
        assertThat(copia.getClaimedAmount()).isEqualByComparingTo(original.getClaimedAmount());
        assertThat(copia.getDiscountedAmount()).isEqualByComparingTo(original.getDiscountedAmount());
        assertThat(copia.getStatus()).isEqualTo(original.getStatus());
        assertThat(copia.getCreatedAt()).isEqualTo(original.getCreatedAt());
        assertThat(copia.getUpdatedAt()).isEqualTo(original.getUpdatedAt());
        assertThat(copia.getPaidAt()).isEqualTo(original.getPaidAt());
    }

    @Test
    void getPayableAmount_retornaTotalMenosDescuento() {
        // Arrange
        var snacks = List.of(snackLine(1, UNIT_SNACK));
        var tickets = List.of(ticketLine(2, UNIT_TICKET));
        BigDecimal descuento = new BigDecimal("10.00");
        Sale sale = new Sale(CLIENT_ID, CINEMA_ID, descuento, snacks, tickets);
        // Act
        BigDecimal pagable = sale.getPayableAmount();
        // Assert
        BigDecimal esperado = UNIT_SNACK.multiply(BigDecimal.valueOf(1)).add(UNIT_TICKET.multiply(BigDecimal.valueOf(2))).subtract(descuento);
        assertThat(pagable).isEqualByComparingTo(esperado);
    }

    @Test
    void sumClaimedAmount_incrementaMonto_sinSuperarTotal() {
        // Arrange
        var sale = new Sale(CLIENT_ID, CINEMA_ID, BigDecimal.ZERO, List.of(snackLine(1, UNIT_SNACK)), List.of(ticketLine(1, UNIT_TICKET)));
        BigDecimal incremento = new BigDecimal("5.00");
        // Act
        sale.sumClaimedAmount(incremento);
        // Assert
        assertThat(sale.getClaimedAmount()).isEqualByComparingTo(incremento);
        assertThat(sale.getUpdatedAt()).isNotNull();
    }

    @Test
    void sumClaimedAmount_lanzaExcepcion_cuandoSuperaTotal() {
        // Arrange
        var sale = new Sale(CLIENT_ID, CINEMA_ID, BigDecimal.ZERO, List.of(snackLine(1, UNIT_SNACK)), List.of(ticketLine(1, UNIT_TICKET)));
        BigDecimal mayorQueTotal = sale.getTotalAmount().add(BigDecimal.ONE);
        // Act / Assert
        assertThatThrownBy(() -> sale.sumClaimedAmount(mayorQueTotal)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void validate_ok_conDatosValidos() {
        // Arrange
        var sale = new Sale(CLIENT_ID, CINEMA_ID, BigDecimal.ZERO, List.of(snackLine(1, UNIT_SNACK)), List.of(ticketLine(1, UNIT_TICKET)));
        // Act
        sale.validate();
        // Assert
        assertThat(sale.getTotalAmount()).isPositive();
    }

    @Test
    void validate_lanza_cuandoClientIdEsNull() {
        // Arrange
        var sale = buildSaleWithStatus(SaleStatusType.PENDING);
        sale = new Sale(sale.getId(), null, sale.getCinemaId(), sale.getTotalAmount(), sale.getClaimedAmount(), sale.getDiscountedAmount(), sale.getStatus(), sale.getCreatedAt(), sale.getUpdatedAt(), sale.getPaidAt());
        sale.setSaleLineSnacks(List.of());
        sale.setSaleLineTickets(List.of());
        // Act / Assert
        assertThatThrownBy(sale::validate).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void validate_lanza_cuandoDescuentoNegativo_oMayorQueTotal() {
        // Arrange
        var base = buildSaleWithStatus(SaleStatusType.PENDING);
        base.setSaleLineSnacks(List.of());
        base.setSaleLineTickets(List.of());
        var negativo = new Sale(base.getId(), base.getClientId(), base.getCinemaId(), new BigDecimal("50"), base.getClaimedAmount(), new BigDecimal("-1"), base.getStatus(), base.getCreatedAt(), base.getUpdatedAt(), base.getPaidAt());
        negativo.setSaleLineSnacks(List.of());
        negativo.setSaleLineTickets(List.of());
        var mayor = new Sale(base.getId(), base.getClientId(), base.getCinemaId(), new BigDecimal("10"), base.getClaimedAmount(), new BigDecimal("20"), base.getStatus(), base.getCreatedAt(), base.getUpdatedAt(), base.getPaidAt());
        mayor.setSaleLineSnacks(List.of());
        mayor.setSaleLineTickets(List.of());
        // Act / Assert
        assertThatThrownBy(negativo::validate).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(mayor::validate).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void validate_lanza_cuandoClaimedNull_Negativo_oMayorQueTotal() {
        // Arrange
        var base = buildSaleWithStatus(SaleStatusType.PENDING);
        base.setSaleLineSnacks(List.of());
        base.setSaleLineTickets(List.of());
        var reclamadoNull = new Sale(base.getId(), base.getClientId(), base.getCinemaId(), new BigDecimal("10"), null, BigDecimal.ZERO, base.getStatus(), base.getCreatedAt(), base.getUpdatedAt(), base.getPaidAt());
        reclamadoNull.setSaleLineSnacks(List.of());
        reclamadoNull.setSaleLineTickets(List.of());
        var reclamadoNeg = new Sale(base.getId(), base.getClientId(), base.getCinemaId(), new BigDecimal("10"), new BigDecimal("-1"), BigDecimal.ZERO, base.getStatus(), base.getCreatedAt(), base.getUpdatedAt(), base.getPaidAt());
        reclamadoNeg.setSaleLineSnacks(List.of());
        reclamadoNeg.setSaleLineTickets(List.of());
        var reclamadoMayor = new Sale(base.getId(), base.getClientId(), base.getCinemaId(), new BigDecimal("10"), new BigDecimal("20"), BigDecimal.ZERO, base.getStatus(), base.getCreatedAt(), base.getUpdatedAt(), base.getPaidAt());
        reclamadoMayor.setSaleLineSnacks(List.of());
        reclamadoMayor.setSaleLineTickets(List.of());
        // Act / Assert
        assertThatThrownBy(reclamadoNull::validate).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(reclamadoNeg::validate).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(reclamadoMayor::validate).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void validate_lanza_cuandoTotalNegativo() {
        // Arrange
        var base = buildSaleWithStatus(SaleStatusType.PENDING);
        var invalida = new Sale(base.getId(), base.getClientId(), base.getCinemaId(), new BigDecimal("-1"), BigDecimal.ZERO, BigDecimal.ZERO, base.getStatus(), base.getCreatedAt(), base.getUpdatedAt(), base.getPaidAt());
        invalida.setSaleLineSnacks(List.of());
        invalida.setSaleLineTickets(List.of());
        // Act / Assert
        assertThatThrownBy(invalida::validate).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void markAsPaid_desdePending_cambiaEstado_ySeteaFechas() {
        // Arrange
        Sale sale = buildSaleWithStatus(SaleStatusType.PENDING);
        // Act
        sale.markAsPaid();
        // Assert
        assertThat(sale.getStatus()).isEqualTo(SaleStatusType.PAID);
        assertThat(sale.getPaidAt()).isNotNull();
        assertThat(sale.getUpdatedAt()).isNotNull();
    }

    @Test
    void markAsPaid_desdeNoPending_lanzaExcepcion() {
        // Arrange
        Sale sale = buildSaleWithStatus(SaleStatusType.CANCELLED);
        // Act / Assert
        assertThatThrownBy(sale::markAsPaid).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void markAsPaidError_desdePending_cambiaAPaidError() {
        // Arrange
        Sale sale = buildSaleWithStatus(SaleStatusType.PENDING);
        // Act
        sale.markAsPaidError();
        // Assert
        assertThat(sale.getStatus()).isEqualTo(SaleStatusType.PAID_ERROR);
        assertThat(sale.getUpdatedAt()).isNotNull();
    }

    @Test
    void markAsPaidError_desdeNoPending_lanzaExcepcion() {
        // Arrange
        Sale sale = buildSaleWithStatus(SaleStatusType.PAID);
        // Act / Assert
        assertThatThrownBy(sale::markAsPaidError).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void cancel_desdePending_oPaidError_cambiaACancelled() {
        // Arrange
        Sale pending = buildSaleWithStatus(SaleStatusType.PENDING);
        Sale paidError = buildSaleWithStatus(SaleStatusType.PAID_ERROR);
        // Act
        pending.cancel();
        paidError.cancel();
        // Assert
        assertThat(pending.getStatus()).isEqualTo(SaleStatusType.CANCELLED);
        assertThat(paidError.getStatus()).isEqualTo(SaleStatusType.CANCELLED);
    }

    @Test
    void cancel_desdeOtroEstado_lanzaExcepcion() {
        // Arrange
        Sale sale = buildSaleWithStatus(SaleStatusType.PAID);
        // Act / Assert
        assertThatThrownBy(sale::cancel).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void pending_desdePaidError_cambiaAPending() {
        // Arrange
        Sale sale = buildSaleWithStatus(SaleStatusType.PAID_ERROR);
        // Act
        sale.pending();
        // Assert
        assertThat(sale.getStatus()).isEqualTo(SaleStatusType.PENDING);
    }

    @Test
    void pending_desdeOtroEstado_lanzaExcepcion() {
        // Arrange
        Sale sale = buildSaleWithStatus(SaleStatusType.PAID);
        // Act / Assert
        assertThatThrownBy(sale::pending).isInstanceOf(IllegalStateException.class);
    }
}