

package com.sap.sales_service.sale.application.usecases.updatestateticket;

import com.sap.common_lib.common.enums.sale.TicketStatusType;
import com.sap.common_lib.exception.NonRetryableBusinessException;
import com.sap.sales_service.sale.application.ouput.FindSaleLineTicketPort;
import com.sap.sales_service.sale.application.ouput.FindSalePort;
import com.sap.sales_service.sale.application.ouput.SaveSaleLineTicketPort;
import com.sap.sales_service.sale.application.ouput.SendNotificationPort;
import com.sap.sales_service.sale.domain.Sale;
import com.sap.sales_service.sale.domain.SaleLineTicket;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UpdateTicketStateSaleTest {

    @Mock private SaveSaleLineTicketPort saveSaleLineTicketPort;
    @Mock private FindSaleLineTicketPort findSaleLineTicketPort;
    @Mock private SendNotificationPort sendNotificationPort;
    @Mock private FindSalePort findSalePort;

    @InjectMocks private UpdateTicketStateSale useCase;

    @Test
    void updateTicketState_shouldThrow_whenSaleLineTicketNotFound() {
        // given
        UUID lineId = UUID.randomUUID();
        given(findSaleLineTicketPort.findById(lineId)).willReturn(Optional.empty());

        // when + then
        assertThatThrownBy(() ->
                useCase.updateTicketState(lineId, TicketStatusType.IN_USE, "msg"))
            .isInstanceOf(NonRetryableBusinessException.class)
            .hasMessageContaining(lineId.toString());
    }

    @Test
    void updateTicketState_shouldSetInUse_saveAndNotify() {
        // given
        UUID lineId = UUID.randomUUID();
        UUID saleId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        SaleLineTicket ticket = mock(SaleLineTicket.class);
        given(ticket.getSaleId()).willReturn(saleId);

        given(findSaleLineTicketPort.findById(lineId)).willReturn(Optional.of(ticket));

        Sale sale = mock(Sale.class);
        given(sale.getClientId()).willReturn(clientId);
        given(findSalePort.findById(saleId)).willReturn(Optional.of(sale));

        // when
        useCase.updateTicketState(lineId, TicketStatusType.IN_USE, "Usando ticket");

        // then
        verify(ticket).use();
        verify(saveSaleLineTicketPort).save(ticket);
        verify(sendNotificationPort).sendNotification(clientId, "Usando ticket");
    }

    @Test
    void updateTicketState_shouldSetReserved_saveAndNotify() {
        // given
        UUID lineId = UUID.randomUUID();
        UUID saleId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        SaleLineTicket ticket = mock(SaleLineTicket.class);
        given(ticket.getSaleId()).willReturn(saleId);
        given(findSaleLineTicketPort.findById(lineId)).willReturn(Optional.of(ticket));

        Sale sale = mock(Sale.class);
        given(sale.getClientId()).willReturn(clientId);
        given(findSalePort.findById(saleId)).willReturn(Optional.of(sale));

        // when
        useCase.updateTicketState(lineId, TicketStatusType.RESERVED, "Reservado");

        // then
        verify(ticket).reserve();
        verify(saveSaleLineTicketPort).save(ticket);
        verify(sendNotificationPort).sendNotification(clientId, "Reservado");
    }

    @Test
    void updateTicketState_withInvalidStatus_shouldOnlyNotifyInvalidUpdate() {
        // given
        UUID lineId = UUID.randomUUID();
        UUID saleId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        SaleLineTicket ticket = mock(SaleLineTicket.class);
        given(ticket.getSaleId()).willReturn(saleId);
        given(findSaleLineTicketPort.findById(lineId)).willReturn(Optional.of(ticket));

        Sale sale = mock(Sale.class);
        given(sale.getClientId()).willReturn(clientId);
        given(findSalePort.findById(saleId)).willReturn(Optional.of(sale));

        // when
        useCase.updateTicketState(lineId, TicketStatusType.CANCELLED, "otro mensaje");

        // then
        verify(ticket, never()).use();
        verify(ticket, never()).reserve();
        verify(saveSaleLineTicketPort, never()).save(any());
        verify(sendNotificationPort).sendNotification(clientId, "La actualización de estado no es válida.");
    }

    @Test
    void updateTicketState_whenUseThrows_shouldCatch_andNotifyWithErrorMessage() {
        // given
        UUID lineId = UUID.randomUUID();
        UUID saleId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        SaleLineTicket ticket = mock(SaleLineTicket.class);
        given(ticket.getSaleId()).willReturn(saleId);
        willThrow(new IllegalStateException("Only pending tickets can be used"))
                .given(ticket).use();
        given(findSaleLineTicketPort.findById(lineId)).willReturn(Optional.of(ticket));

        Sale sale = mock(Sale.class);
        given(sale.getClientId()).willReturn(clientId);
        given(findSalePort.findById(saleId)).willReturn(Optional.of(sale));

        // when
        useCase.updateTicketState(lineId, TicketStatusType.IN_USE, "Intento de uso");

        // then
        // verify no save since use() failed
        verify(saveSaleLineTicketPort, never()).save(any());
        // verify the message is concatenated with exception message
        ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);
        verify(sendNotificationPort).sendNotification(eq(clientId), msgCaptor.capture());
        // Message should contain both original and exception message separated by ","
        String sent = msgCaptor.getValue();
        org.assertj.core.api.Assertions.assertThat(sent)
                .contains("Intento de uso")
                .contains("Only pending tickets can be used");
    }

    @Test
    void updateTicketState_shouldNotNotify_whenSaleNotFound() {
        // given
        UUID lineId = UUID.randomUUID();
        UUID saleId = UUID.randomUUID();

        SaleLineTicket ticket = mock(SaleLineTicket.class);
        given(ticket.getSaleId()).willReturn(saleId);
        given(findSaleLineTicketPort.findById(lineId)).willReturn(Optional.of(ticket));

        given(findSalePort.findById(saleId)).willReturn(Optional.empty());

        // when
        useCase.updateTicketState(lineId, TicketStatusType.RESERVED, "msg");

        // then
        verify(ticket).reserve();
        verify(saveSaleLineTicketPort).save(ticket);
        verify(sendNotificationPort, never()).sendNotification(any(), any());
    }

    @Test
    void updateTicketState_shouldNotNotify_whenClientIdIsNull() {
        // given
        UUID lineId = UUID.randomUUID();
        UUID saleId = UUID.randomUUID();

        SaleLineTicket ticket = mock(SaleLineTicket.class);
        given(ticket.getSaleId()).willReturn(saleId);
        given(findSaleLineTicketPort.findById(lineId)).willReturn(Optional.of(ticket));

        Sale sale = mock(Sale.class);
        given(sale.getClientId()).willReturn(null);
        given(findSalePort.findById(saleId)).willReturn(Optional.of(sale));

        // when
        useCase.updateTicketState(lineId, TicketStatusType.IN_USE, "msg");

        // then
        verify(ticket).use();
        verify(saveSaleLineTicketPort).save(ticket);
        verify(sendNotificationPort, never()).sendNotification(any(), any());
    }
}