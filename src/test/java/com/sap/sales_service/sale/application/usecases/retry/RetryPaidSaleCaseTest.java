

package com.sap.sales_service.sale.application.usecases.retry;

import com.sap.common_lib.common.enums.sale.SaleStatusType;
import com.sap.sales_service.sale.application.ouput.FindSalePort;
import com.sap.sales_service.sale.application.ouput.SaveSalePort;
import com.sap.sales_service.sale.application.ouput.SendNotificationPort;
import com.sap.sales_service.sale.application.ouput.SendPaidRequestPort;
import com.sap.sales_service.sale.domain.Sale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class RetryPaidSaleCaseTest {

    @Mock private FindSalePort findSalePort;
    @Mock private SaveSalePort saveSalePort;
    @Mock private SendPaidRequestPort sendPaidRequestPort;
    @Mock private SendNotificationPort sendNotificationPort;

    @InjectMocks private RetryPaidSaleCase caseUnderTest;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    private Sale buildSale(
            UUID id,
            UUID clientId,
            SaleStatusType status,
            LocalDateTime paidAt
    ) {
        // Minimal valid Sale using the full-args ctor from the domain
        return new Sale(
                id,
                clientId,
                UUID.randomUUID(),
                new BigDecimal("100.00"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                status,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1),
                paidAt
        );
    }

    @Test
    void retryPaidSale_shouldThrow_whenSaleNotFound() {
        UUID saleId = UUID.randomUUID();
        given(findSalePort.findById(saleId)).willReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> caseUnderTest.retryPaidSale(saleId));

        verify(findSalePort).findById(saleId);
        verifyNoInteractions(saveSalePort, sendPaidRequestPort, sendNotificationPort);
    }

    @Test
    void retryPaidSale_shouldThrow_whenAlreadyPaid() {
        UUID saleId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        Sale sale = buildSale(saleId, clientId, SaleStatusType.PAID, LocalDateTime.now());
        given(findSalePort.findById(saleId)).willReturn(Optional.of(sale));

        assertThrows(IllegalStateException.class, () -> caseUnderTest.retryPaidSale(saleId));

        verify(findSalePort).findById(saleId);
        verifyNoInteractions(saveSalePort, sendPaidRequestPort, sendNotificationPort);
    }

    @Test
    void retryPaidSale_shouldThrow_whenStatusNotPaidError() {
        UUID saleId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        Sale sale = buildSale(saleId, clientId, SaleStatusType.PENDING, null);
        given(findSalePort.findById(saleId)).willReturn(Optional.of(sale));

        assertThrows(IllegalStateException.class, () -> caseUnderTest.retryPaidSale(saleId));

        verify(findSalePort).findById(saleId);
        verifyNoInteractions(saveSalePort, sendPaidRequestPort, sendNotificationPort);
    }

    @Test
    void retryPaidSale_shouldSucceed_whenPaidError_andNotifyClient() {
        UUID saleId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        Sale sale = buildSale(saleId, clientId, SaleStatusType.PAID_ERROR, null);
        given(findSalePort.findById(saleId)).willReturn(Optional.of(sale));
        // echo-save
        given(saveSalePort.save(any(Sale.class))).willAnswer(inv -> inv.getArgument(0));

        caseUnderTest.retryPaidSale(saleId);

        // Sale must be set to PENDING before save
        ArgumentCaptor<Sale> saleCaptor = ArgumentCaptor.forClass(Sale.class);
        verify(saveSalePort).save(saleCaptor.capture());
        assertThat(saleCaptor.getValue().getStatus()).isEqualTo(SaleStatusType.PENDING);

        verify(sendPaidRequestPort).sendPaidRequest(eq(clientId), eq(saleId), eq(new BigDecimal("100.00")));
        verify(sendNotificationPort).sendNotification(eq(clientId), contains(saleId.toString()));
        verify(findSalePort).findById(saleId);
        verifyNoMoreInteractions(sendPaidRequestPort, sendNotificationPort, saveSalePort);
    }

    @Test
    void retryPaidSale_shouldSucceed_andSkipNotification_whenClientIdNull() {
        UUID saleId = UUID.randomUUID();
        Sale sale = buildSale(saleId, null, SaleStatusType.PAID_ERROR, null);
        given(findSalePort.findById(saleId)).willReturn(Optional.of(sale));
        given(saveSalePort.save(any(Sale.class))).willAnswer(inv -> inv.getArgument(0));

        caseUnderTest.retryPaidSale(saleId);

        verify(saveSalePort).save(any(Sale.class));
        verify(sendPaidRequestPort).sendPaidRequest(isNull(), eq(saleId), eq(new BigDecimal("100.00")));
        verify(sendNotificationPort, never()).sendNotification(any(), any());
        verify(findSalePort).findById(saleId);
        verifyNoMoreInteractions(sendPaidRequestPort, sendNotificationPort, saveSalePort);
    }
}