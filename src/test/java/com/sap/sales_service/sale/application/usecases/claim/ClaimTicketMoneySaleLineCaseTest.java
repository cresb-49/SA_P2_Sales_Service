package com.sap.sales_service.sale.application.usecases.claim;

import com.sap.common_lib.common.enums.sale.SaleStatusType;
import com.sap.common_lib.common.enums.sale.TicketStatusType;
import com.sap.common_lib.exception.NotFoundException;
import com.sap.sales_service.sale.application.ouput.FindSaleLineTicketPort;
import com.sap.sales_service.sale.application.ouput.FindSalePort;
import com.sap.sales_service.sale.application.ouput.FindTicketPort;
import com.sap.sales_service.sale.application.ouput.RefoundAmountRequestPort;
import com.sap.sales_service.sale.application.ouput.SaveSaleLineTicketPort;
import com.sap.sales_service.sale.application.ouput.SaveSalePort;
import com.sap.sales_service.sale.domain.Sale;
import com.sap.sales_service.sale.domain.SaleLineTicket;
import com.sap.sales_service.sale.domain.dtos.TicketView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ClaimTicketMoneySaleLineCaseTest {

    private SaveSalePort saveSalePort;
    private SaveSaleLineTicketPort saveSaleLineTicketPort;
    private FindSalePort findSalePort;
    private FindSaleLineTicketPort findSaleLineTicketPort;
    private FindTicketPort findTicketPort;
    private RefoundAmountRequestPort refoundAmountRequestPort;

    private ClaimTicketMoneySaleLineCase useCase;

    // Constantes
    private static final UUID SALE_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID SALE_LINE_ID = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    private static final UUID USER_ID = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
    private static final UUID CINEMA_ID = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");
    private static final BigDecimal UNIT_PRICE = new BigDecimal("25.00");
    private static final int QUANTITY = 2;
    private static final BigDecimal TOTAL_PRICE = UNIT_PRICE.multiply(BigDecimal.valueOf(QUANTITY));
    private static final LocalDateTime NOW = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        saveSalePort = mock(SaveSalePort.class);
        saveSaleLineTicketPort = mock(SaveSaleLineTicketPort.class);
        findSalePort = mock(FindSalePort.class);
        findSaleLineTicketPort = mock(FindSaleLineTicketPort.class);
        findTicketPort = mock(FindTicketPort.class);
        refoundAmountRequestPort = mock(RefoundAmountRequestPort.class);

        useCase = new ClaimTicketMoneySaleLineCase(
                saveSalePort,
                saveSaleLineTicketPort,
                findSalePort,
                findSaleLineTicketPort,
                findTicketPort,
                refoundAmountRequestPort
        );
    }

    @Test
    void claimTicketMoneySaleLine_shouldSucceed_andRefund_whenPaid_andTicketNotUsed() {
        // Arrange
        var line = new SaleLineTicket(
                SALE_LINE_ID, SALE_ID, QUANTITY, UNIT_PRICE, TOTAL_PRICE,
                TicketStatusType.PENDING, NOW, NOW
        );
        var sale = new Sale(
                SALE_ID, USER_ID, CINEMA_ID,
                TOTAL_PRICE, BigDecimal.ZERO, BigDecimal.ZERO,
                SaleStatusType.PAID, NOW, NOW, NOW
        );
        TicketView tv = mock(TicketView.class);
        given(tv.used()).willReturn(false);
        given(tv.saleLineTicketId()).willReturn(SALE_LINE_ID);

        given(findSaleLineTicketPort.findById(SALE_LINE_ID)).willReturn(Optional.of(line));
        given(findSalePort.findById(SALE_ID)).willReturn(Optional.of(sale));
        given(findTicketPort.findBySaleLineTicketId(SALE_LINE_ID)).willReturn(Optional.of(tv));

        // Act
        useCase.claimTicketMoneySaleLine(SALE_LINE_ID);

        // Assert
        ArgumentCaptor<SaleLineTicket> captorLine = ArgumentCaptor.forClass(SaleLineTicket.class);
        verify(saveSaleLineTicketPort).save(captorLine.capture());
        verify(saveSalePort).save(any(Sale.class));
        verify(refoundAmountRequestPort).requestRefoundAmount(eq(TOTAL_PRICE), eq(USER_ID), anyString());
        assert captorLine.getValue().getStatus() == TicketStatusType.CANCELLED;
    }

    @Test
    void claimTicketMoneySaleLine_shouldThrow_whenSaleLineNotFound() {
        // Arrange
        given(findSaleLineTicketPort.findById(SALE_LINE_ID)).willReturn(Optional.empty());

        // Act
        var call = (Runnable) () -> useCase.claimTicketMoneySaleLine(SALE_LINE_ID);

        // Assert
        assertThatThrownBy(call::run).isInstanceOf(NotFoundException.class);
    }

    @Test
    void claimTicketMoneySaleLine_shouldThrow_whenSaleLineAlreadyCancelled() {
        // Arrange
        var line = new SaleLineTicket(
                SALE_LINE_ID, SALE_ID, QUANTITY, UNIT_PRICE, TOTAL_PRICE,
                TicketStatusType.CANCELLED, NOW, NOW
        );
        given(findSaleLineTicketPort.findById(SALE_LINE_ID)).willReturn(Optional.of(line));

        // Act
        var call = (Runnable) () -> useCase.claimTicketMoneySaleLine(SALE_LINE_ID);

        // Assert
        assertThatThrownBy(call::run).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void claimTicketMoneySaleLine_shouldThrow_whenSaleNotFound() {
        // Arrange
        var line = new SaleLineTicket(
                SALE_LINE_ID, SALE_ID, QUANTITY, UNIT_PRICE, TOTAL_PRICE,
                TicketStatusType.PENDING, NOW, NOW
        );
        given(findSaleLineTicketPort.findById(SALE_LINE_ID)).willReturn(Optional.of(line));
        given(findSalePort.findById(SALE_ID)).willReturn(Optional.empty());

        // Act
        var call = (Runnable) () -> useCase.claimTicketMoneySaleLine(SALE_LINE_ID);

        // Assert
        assertThatThrownBy(call::run).isInstanceOf(NotFoundException.class);
    }

    @Test
    void claimTicketMoneySaleLine_shouldThrow_whenSaleIsNotPaid() {
        // Arrange
        var line = new SaleLineTicket(
                SALE_LINE_ID, SALE_ID, QUANTITY, UNIT_PRICE, TOTAL_PRICE,
                TicketStatusType.PENDING, NOW, NOW
        );
        var sale = new Sale(
                SALE_ID, USER_ID, CINEMA_ID,
                TOTAL_PRICE, BigDecimal.ZERO, BigDecimal.ZERO,
                SaleStatusType.PENDING, NOW, NOW, null
        );
        given(findSaleLineTicketPort.findById(SALE_LINE_ID)).willReturn(Optional.of(line));
        given(findSalePort.findById(SALE_ID)).willReturn(Optional.of(sale));

        // Act
        var call = (Runnable) () -> useCase.claimTicketMoneySaleLine(SALE_LINE_ID);

        // Assert
        assertThatThrownBy(call::run).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void claimTicketMoneySaleLine_shouldThrow_whenTicketNotFound() {
        // Arrange
        var line = new SaleLineTicket(
                SALE_LINE_ID, SALE_ID, QUANTITY, UNIT_PRICE, TOTAL_PRICE,
                TicketStatusType.PENDING, NOW, NOW
        );
        var sale = new Sale(
                SALE_ID, USER_ID, CINEMA_ID,
                TOTAL_PRICE, BigDecimal.ZERO, BigDecimal.ZERO,
                SaleStatusType.PAID, NOW, NOW, NOW
        );
        given(findSaleLineTicketPort.findById(SALE_LINE_ID)).willReturn(Optional.of(line));
        given(findSalePort.findById(SALE_ID)).willReturn(Optional.of(sale));
        given(findTicketPort.findBySaleLineTicketId(SALE_LINE_ID)).willReturn(Optional.empty());

        // Act
        var call = (Runnable) () -> useCase.claimTicketMoneySaleLine(SALE_LINE_ID);

        // Assert
        assertThatThrownBy(call::run).isInstanceOf(NotFoundException.class);
    }

    @Test
    void claimTicketMoneySaleLine_shouldThrow_whenTicketUsed() {
        // Arrange
        var line = new SaleLineTicket(
                SALE_LINE_ID, SALE_ID, QUANTITY, UNIT_PRICE, TOTAL_PRICE,
                TicketStatusType.PENDING, NOW, NOW
        );
        var sale = new Sale(
                SALE_ID, USER_ID, CINEMA_ID,
                TOTAL_PRICE, BigDecimal.ZERO, BigDecimal.ZERO,
                SaleStatusType.PAID, NOW, NOW, NOW
        );
        TicketView tv = mock(TicketView.class);
        given(tv.used()).willReturn(true);
        given(tv.saleLineTicketId()).willReturn(SALE_LINE_ID);

        given(findSaleLineTicketPort.findById(SALE_LINE_ID)).willReturn(Optional.of(line));
        given(findSalePort.findById(SALE_ID)).willReturn(Optional.of(sale));
        given(findTicketPort.findBySaleLineTicketId(SALE_LINE_ID)).willReturn(Optional.of(tv));

        // Act
        var call = (Runnable) () -> useCase.claimTicketMoneySaleLine(SALE_LINE_ID);

        // Assert
        assertThatThrownBy(call::run).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void claimTicketMoneySaleLine_shouldNotRequestRefund_whenUserIsNull() {
        // Arrange
        var line = new SaleLineTicket(
                SALE_LINE_ID, SALE_ID, QUANTITY, UNIT_PRICE, TOTAL_PRICE,
                TicketStatusType.PENDING, NOW, NOW
        );
        var sale = new Sale(
                SALE_ID, null, CINEMA_ID,
                TOTAL_PRICE, BigDecimal.ZERO, BigDecimal.ZERO,
                SaleStatusType.PAID, NOW, NOW, NOW
        );
        TicketView tv = mock(TicketView.class);
        given(tv.used()).willReturn(false);
        given(tv.saleLineTicketId()).willReturn(SALE_LINE_ID);

        given(findSaleLineTicketPort.findById(SALE_LINE_ID)).willReturn(Optional.of(line));
        given(findSalePort.findById(SALE_ID)).willReturn(Optional.of(sale));
        given(findTicketPort.findBySaleLineTicketId(SALE_LINE_ID)).willReturn(Optional.of(tv));

        // Act
        useCase.claimTicketMoneySaleLine(SALE_LINE_ID);

        // Assert
        verify(saveSaleLineTicketPort).save(any(SaleLineTicket.class));
        verify(saveSalePort).save(any(Sale.class));
        verify(refoundAmountRequestPort, never()).requestRefoundAmount(any(), any(), anyString());
    }
}