package com.sap.sales_service.sale.application.usecases.updatestatesale;

import com.sap.common_lib.exception.NonRetryableBusinessException;
import com.sap.common_lib.exception.NotFoundException;
import com.sap.sales_service.sale.application.ouput.FindSaleLineTicketPort;
import com.sap.sales_service.sale.application.ouput.FindSalePort;
import com.sap.sales_service.sale.application.ouput.SaveSaleLineTicketPort;
import com.sap.sales_service.sale.application.ouput.SaveSalePort;
import com.sap.sales_service.sale.application.ouput.SendNotificationPort;
import com.sap.sales_service.sale.application.usecases.updatestatesale.dtos.UpdateStateSaleDTO;
import com.sap.sales_service.sale.domain.Sale;
import com.sap.sales_service.sale.domain.SaleLineTicket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.sap.common_lib.common.enums.sale.SaleStatusType.PENDING;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateStateSaleCaseTest {

    @Mock private FindSalePort findSalePort;
    @Mock private SaveSalePort saveSalePort;
    @Mock private SendNotificationPort sendNotificationPort;
    @Mock private FindSaleLineTicketPort findSaleLineTicketPort;
    @Mock private SaveSaleLineTicketPort saveSaleLineTicketPort;

    @InjectMocks private UpdateStateSaleCase useCase;

    private UUID saleId;
    private UUID clientId;

    @BeforeEach
    void setUp() {
        saleId = UUID.randomUUID();
        clientId = UUID.randomUUID();
    }

    private Sale newPendingSale(UUID id, UUID clientId) {
        return new Sale(
                id,
                clientId,
                UUID.randomUUID(),
                BigDecimal.TEN,                // totalAmount
                BigDecimal.ZERO,               // claimedAmount
                BigDecimal.ZERO,               // discountedAmount
                PENDING,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().minusMinutes(30),
                null                           // paidAt
        );
    }

    @Test
    void updateStateSale_shouldThrow_whenSaleNotFound() {
        // given
        var dto = new UpdateStateSaleDTO(saleId, true,"Payment successful");
        given(findSalePort.findById(saleId)).willReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> useCase.updateStateSale(dto))
                .isInstanceOf(NotFoundException.class);

        verifyNoInteractions(saveSalePort, sendNotificationPort, findSaleLineTicketPort, saveSaleLineTicketPort);
    }

    @Test
    void updateStateSale_shouldMarkPaid_purchaseAllTickets_saveAndNotify() {
        // given
        var dto = new UpdateStateSaleDTO(saleId, true,"Payment successful");
        var sale = newPendingSale(saleId, clientId);
        given(findSalePort.findById(saleId)).willReturn(Optional.of(sale));

        // two line tickets
        var lt1 = mock(SaleLineTicket.class);
        var lt2 = mock(SaleLineTicket.class);
        given(findSaleLineTicketPort.findAllBySaleId(saleId)).willReturn(List.of(lt1, lt2));

        // when
        useCase.updateStateSale(dto);

        // then
        verify(findSaleLineTicketPort).findAllBySaleId(saleId);
        verify(lt1).purchase();
        verify(lt2).purchase();
        verify(saveSaleLineTicketPort, times(2)).save(any(SaleLineTicket.class));
        verify(saveSalePort).save(sale);
        verify(sendNotificationPort).sendNotification(eq(clientId), any(String.class));
        verifyNoMoreInteractions(sendNotificationPort);
    }

    @Test
    void updateStateSale_paid_shouldNotNotify_whenClientIsNull() {
        // given
        var dto = new UpdateStateSaleDTO(saleId, true,"Payment successful");
        var sale = newPendingSale(saleId, null);
        given(findSalePort.findById(saleId)).willReturn(Optional.of(sale));
        given(findSaleLineTicketPort.findAllBySaleId(saleId)).willReturn(List.of());

        // when
        useCase.updateStateSale(dto);

        // then
        verify(saveSalePort).save(sale);
        verifyNoInteractions(sendNotificationPort);
    }

    @Test
    void updateStateSale_shouldMarkPaidError_reserveTickets_skipFailures_andNotify() {
        // given
        var dto = new UpdateStateSaleDTO(saleId, false,"Payment failed");
        var sale = newPendingSale(saleId, clientId);
        given(findSalePort.findById(saleId)).willReturn(Optional.of(sale));

        var ok = mock(SaleLineTicket.class);
        var failing = mock(SaleLineTicket.class);
        // reserve() is void -> use doThrow
        doThrow(new RuntimeException("cannot reserve")).when(failing).reserve();
        given(findSaleLineTicketPort.findAllBySaleId(saleId)).willReturn(List.of(ok, failing));

        // when
        useCase.updateStateSale(dto);

        // then: ok reserved & saved once, failing skipped, sale saved & notified
        verify(ok).reserve();
        verify(saveSaleLineTicketPort).save(ok);
        verify(failing).reserve();
        verify(saveSaleLineTicketPort, never()).save(failing);
        verify(saveSalePort).save(sale);
        verify(sendNotificationPort).sendNotification(eq(clientId), any(String.class));
    }
}
