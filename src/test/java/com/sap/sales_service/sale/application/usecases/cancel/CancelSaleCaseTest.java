package com.sap.sales_service.sale.application.usecases.cancel;

import com.sap.common_lib.common.enums.sale.SaleStatusType;
import com.sap.common_lib.common.enums.sale.TicketStatusType;
import com.sap.sales_service.sale.application.factory.SaleLineTicketFactory;
import com.sap.sales_service.sale.application.ouput.*;
import com.sap.sales_service.sale.domain.Sale;
import com.sap.sales_service.sale.domain.SaleLineTicket;
import com.sap.sales_service.sale.domain.SaleLineSnack;
import com.sap.sales_service.sale.domain.dtos.TicketView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * Reglas de estilo de estos tests:
 * // Arrange
 * // Act
 * // Assert
 * No se validan mensajes de excepciones y se usan constantes.
 */
@ExtendWith(MockitoExtension.class)
class CancelSaleCaseTest {

    // Constantes
    private static final UUID SALE_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID CLIENT_ID = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    private static final BigDecimal TOTAL = new BigDecimal("200.00");
    private static final BigDecimal CLAIMED = new BigDecimal("100.00");
    private static final BigDecimal DISCOUNTED = new BigDecimal("50.00");
    private static final LocalDateTime NOW = LocalDateTime.of(2024, 1, 1, 12, 0);

    @Mock private FindSalePort findSalePort;
    @Mock private SaveSalePort saveSalePort;
    @Mock private FindSaleLineTicketPort findSaleLineTicketPort;
    @Mock private FindSaleLineSnackPort findSaleLineSnackPort;
    @Mock private SaveSaleLineTicketPort saveSaleLineTicketPort;
    @Mock private RefoundAmountRequestPort refoundAmountRequestPort;
    @Mock private SaleLineTicketFactory saleLineTicketFactory;
    @Mock private SendNotificationPort sendNotificationPort;

    @InjectMocks private CancelSaleCase cancelSaleCase;

    private Sale baseSale(SaleStatusType status, BigDecimal total, BigDecimal claimed, BigDecimal discounted) {
        return new Sale(
                SALE_ID, CLIENT_ID, UUID.randomUUID(),
                total, claimed, discounted,
                status, NOW, NOW, null
        );
    }

    private SaleLineTicket ticket(UUID id, TicketStatusType status, BigDecimal totalPrice) {
        return new SaleLineTicket(
                id, SALE_ID, 1, totalPrice, totalPrice, status, NOW, NOW
        );
    }

    @BeforeEach
    void setup() {
        // No stubbing innecesario
    }

    @Test
    void cancelSaleById_shouldThrow_whenSaleNotFound() {
        // Arrange
        given(findSalePort.findById(SALE_ID)).willReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> cancelSaleCase.cancelSaleById(SALE_ID))
                .isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(saveSalePort, refoundAmountRequestPort, sendNotificationPort);
    }

    @Test
    void cancelSaleById_shouldThrow_whenSaleAlreadyCancelled() {
        // Arrange
        var sale = baseSale(SaleStatusType.CANCELLED, TOTAL, CLAIMED, DISCOUNTED);
        given(findSalePort.findById(SALE_ID)).willReturn(Optional.of(sale));
        // Act & Assert
        assertThatThrownBy(() -> cancelSaleCase.cancelSaleById(SALE_ID))
                .isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(saveSalePort, refoundAmountRequestPort, sendNotificationPort);
    }

    @Test
    void cancelSaleById_shouldThrow_whenSaleIsPending() {
        // Arrange
        var sale = baseSale(SaleStatusType.PENDING, TOTAL, CLAIMED, DISCOUNTED);
        given(findSalePort.findById(SALE_ID)).willReturn(Optional.of(sale));
        // Act & Assert
        assertThatThrownBy(() -> cancelSaleCase.cancelSaleById(SALE_ID))
                .isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(saveSalePort, refoundAmountRequestPort, sendNotificationPort);
    }

    @Test
    void cancelSaleById_shouldThrow_whenPaidAndHasSnacks() {
        // Arrange
        var sale = baseSale(SaleStatusType.PAID, TOTAL, CLAIMED, DISCOUNTED);
        given(findSalePort.findById(SALE_ID)).willReturn(Optional.of(sale));
        given(findSaleLineSnackPort.findAllBySaleId(SALE_ID)).willReturn(List.of(new SaleLineSnack(UUID.randomUUID(), SALE_ID, UUID.randomUUID(), 1, new BigDecimal("5.00"), new BigDecimal("5.00"))));
        // Act & Assert
        assertThatThrownBy(() -> cancelSaleCase.cancelSaleById(SALE_ID))
                .isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(refoundAmountRequestPort);
    }

    @Test
    void cancelSaleById_shouldThrow_whenAnyTicketUsed_onPaidSale() {
        // Arrange
        var sale = baseSale(SaleStatusType.PAID, TOTAL, CLAIMED, DISCOUNTED);
        var t1 = ticket(UUID.fromString("11111111-1111-1111-1111-111111111111"), TicketStatusType.PENDING, new BigDecimal("10.00"));
        var t2 = ticket(UUID.fromString("22222222-2222-2222-2222-222222222222"), TicketStatusType.RESERVED, new BigDecimal("20.00"));

        var tv1 = mock(TicketView.class);
        var tv2 = mock(TicketView.class);
        given(tv1.used()).willReturn(false);
        given(tv2.used()).willReturn(true); // Ticket usado

        t1.setTicketView(tv1);
        t2.setTicketView(tv2);

        given(findSalePort.findById(SALE_ID)).willReturn(Optional.of(sale));
        given(findSaleLineSnackPort.findAllBySaleId(SALE_ID)).willReturn(List.of());
        given(findSaleLineTicketPort.findAllBySaleId(SALE_ID)).willReturn(List.of(t1, t2));
        given(saleLineTicketFactory.saleLineTicketsWithAllRelations(anyList())).willReturn(List.of(t1, t2));

        // Act & Assert
        assertThatThrownBy(() -> cancelSaleCase.cancelSaleById(SALE_ID))
                .isInstanceOf(IllegalStateException.class);
        verify(refoundAmountRequestPort, never()).requestRefoundAmount(any(), any(), anyString());
        verify(saveSaleLineTicketPort, times(1)).save(any());
    }

    @Test
    void cancelSaleById_shouldCancelTickets_sumClaimed_sendRefundAndNotify_whenPaidWithoutSnacks() {
        // Arrange
        var sale = baseSale(SaleStatusType.PAID, TOTAL, CLAIMED, DISCOUNTED);
        var t1 = ticket(UUID.fromString("33333333-3333-3333-3333-333333333333"), TicketStatusType.PENDING, new BigDecimal("10.00"));
        var t2 = ticket(UUID.fromString("44444444-4444-4444-4444-444444444444"), TicketStatusType.RESERVED, new BigDecimal("20.00"));
        var tv1 = mock(TicketView.class);
        var tv2 = mock(TicketView.class);
        given(tv1.used()).willReturn(false);
        given(tv2.used()).willReturn(false);
        t1.setTicketView(tv1);
        t2.setTicketView(tv2);

        given(findSalePort.findById(SALE_ID)).willReturn(Optional.of(sale));
        given(findSaleLineSnackPort.findAllBySaleId(SALE_ID)).willReturn(List.of());
        given(findSaleLineTicketPort.findAllBySaleId(SALE_ID)).willReturn(List.of(t1, t2));
        given(saleLineTicketFactory.saleLineTicketsWithAllRelations(anyList())).willReturn(List.of(t1, t2));

        // Act
        cancelSaleCase.cancelSaleById(SALE_ID);

        // Assert
        verify(saveSaleLineTicketPort, times(2)).save(any(SaleLineTicket.class));
        verify(saveSalePort).save(sale);
        verify(refoundAmountRequestPort, times(1))
                .requestRefoundAmount(any(BigDecimal.class), eq(CLIENT_ID), anyString());
        verify(sendNotificationPort, times(1))
                .sendNotification(eq(CLIENT_ID), anyString());
    }

    @Test
    void cancelSaleById_shouldCancelSaleAndTickets_andNotify_whenNotPaid() {
        // Arrange
        var sale = baseSale(SaleStatusType.PAID_ERROR, TOTAL, CLAIMED, DISCOUNTED);
        var t1 = ticket(UUID.fromString("55555555-5555-5555-5555-555555555555"), TicketStatusType.RESERVED, new BigDecimal("15.00"));
        var t2 = ticket(UUID.fromString("66666666-6666-6666-6666-666666666666"), TicketStatusType.PENDING, new BigDecimal("25.00"));

        given(findSalePort.findById(SALE_ID)).willReturn(Optional.of(sale));
        given(findSaleLineTicketPort.findAllBySaleId(SALE_ID)).willReturn(List.of(t1, t2));

        // Act
        cancelSaleCase.cancelSaleById(SALE_ID);

        // Assert
        verify(saveSalePort).save(sale);
        verify(saveSaleLineTicketPort, times(2)).save(any(SaleLineTicket.class));
        verify(sendNotificationPort).sendNotification(eq(CLIENT_ID), anyString());
        verifyNoInteractions(refoundAmountRequestPort);
    }
}