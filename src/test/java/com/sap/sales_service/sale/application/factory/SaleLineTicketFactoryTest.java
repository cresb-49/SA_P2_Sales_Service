

package com.sap.sales_service.sale.application.factory;

import com.sap.common_lib.common.enums.sale.TicketStatusType;
import com.sap.sales_service.sale.application.ouput.FindSaleLineTicketPort;
import com.sap.sales_service.sale.application.ouput.FindTicketPort;
import com.sap.sales_service.sale.domain.SaleLineTicket;
import com.sap.sales_service.sale.domain.dtos.TicketView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaleLineTicketFactoryTest {

    // Arrange constants
    private static final UUID SALE_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID LINE_ID_1 = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID LINE_ID_2 = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @Mock private FindSaleLineTicketPort findSaleLineTicketPort;
    @Mock private FindTicketPort findTicketPort;

    @InjectMocks private SaleLineTicketFactory factory;

    @Captor private ArgumentCaptor<List<UUID>> uuidListCaptor;

    private SaleLineTicket makeLine(UUID lineId) {
        return new SaleLineTicket(
                lineId,
                SALE_ID,
                2,
                BigDecimal.TEN,
                BigDecimal.TEN.multiply(BigDecimal.valueOf(2)),
                TicketStatusType.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    private TicketView mockTicketView(UUID saleLineTicketId) {
        TicketView view = mock(TicketView.class);
        when(view.saleLineTicketId()).thenReturn(saleLineTicketId);
        return view;
    }

    @Test
    void saleLineTicketWithAllRelations_shouldAttachTicketViews_whenAllFound() {
        // Arrange
        var line1 = makeLine(LINE_ID_1);
        var line2 = makeLine(LINE_ID_2);
        when(findSaleLineTicketPort.findAllBySaleId(SALE_ID)).thenReturn(List.of(line1, line2));

        var view1 = mockTicketView(LINE_ID_1);
        var view2 = mockTicketView(LINE_ID_2);
        when(findTicketPort.findAllBySaleLineTicketIds(anyList())).thenReturn(List.of(view1, view2));

        // Act
        var result = factory.saleLineTicketWithAllRelations(SALE_ID);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTicketView()).isEqualTo(view1);
        assertThat(result.get(1).getTicketView()).isEqualTo(view2);

        verify(findSaleLineTicketPort).findAllBySaleId(SALE_ID);
        verify(findTicketPort).findAllBySaleLineTicketIds(uuidListCaptor.capture());
        assertThat(uuidListCaptor.getValue()).containsExactlyInAnyOrder(LINE_ID_1, LINE_ID_2);
        verifyNoMoreInteractions(findSaleLineTicketPort, findTicketPort);
    }

    @Test
    void saleLineTicketWithAllRelations_shouldLeaveNull_whenSomeMissing() {
        // Arrange
        var line1 = makeLine(LINE_ID_1);
        var line2 = makeLine(LINE_ID_2);
        when(findSaleLineTicketPort.findAllBySaleId(SALE_ID)).thenReturn(List.of(line1, line2));

        var view1 = mockTicketView(LINE_ID_1); // Falta el de LINE_ID_2
        when(findTicketPort.findAllBySaleLineTicketIds(anyList())).thenReturn(List.of(view1));

        // Act
        var result = factory.saleLineTicketWithAllRelations(SALE_ID);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTicketView()).isEqualTo(view1);
        assertThat(result.get(1).getTicketView()).isNull();

        verify(findSaleLineTicketPort).findAllBySaleId(SALE_ID);
        verify(findTicketPort).findAllBySaleLineTicketIds(anyList());
        verifyNoMoreInteractions(findSaleLineTicketPort, findTicketPort);
    }

    @Test
    void saleLineTicketWithAllRelations_single_shouldAttach_whenFound() {
        // Arrange
        var line = makeLine(LINE_ID_1);
        TicketView view = mock(TicketView.class);
        when(findTicketPort.findBySaleLineTicketId(LINE_ID_1)).thenReturn(Optional.of(view));

        // Act
        var result = factory.saleLineTicketWithAllRelations(line);

        // Assert
        assertThat(result.getTicketView()).isEqualTo(view);
        verify(findTicketPort).findBySaleLineTicketId(LINE_ID_1);
        verifyNoMoreInteractions(findTicketPort);
        verifyNoInteractions(findSaleLineTicketPort);
    }

    @Test
    void saleLineTicketWithAllRelations_single_shouldLeaveNull_whenNotFound() {
        // Arrange
        var line = makeLine(LINE_ID_2);
        when(findTicketPort.findBySaleLineTicketId(LINE_ID_2)).thenReturn(Optional.empty());

        // Act
        var result = factory.saleLineTicketWithAllRelations(line);

        // Assert
        assertThat(result.getTicketView()).isNull();
        verify(findTicketPort).findBySaleLineTicketId(LINE_ID_2);
        verifyNoMoreInteractions(findTicketPort);
        verifyNoInteractions(findSaleLineTicketPort);
    }

    @Test
    void saleLineTicketsWithAllRelations_listVariant_shouldAttach_whenFound() {
        // Arrange
        var line1 = makeLine(LINE_ID_1);
        var line2 = makeLine(LINE_ID_2);

        var view1 = mockTicketView(LINE_ID_1);
        var view2 = mockTicketView(LINE_ID_2);
        when(findTicketPort.findAllBySaleLineTicketIds(anyList())).thenReturn(List.of(view1, view2));

        // Act
        var result = factory.saleLineTicketsWithAllRelations(List.of(line1, line2));

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTicketView()).isEqualTo(view1);
        assertThat(result.get(1).getTicketView()).isEqualTo(view2);
        verify(findTicketPort).findAllBySaleLineTicketIds(uuidListCaptor.capture());
        assertThat(uuidListCaptor.getValue()).containsExactlyInAnyOrder(LINE_ID_1, LINE_ID_2);
        verifyNoMoreInteractions(findTicketPort);
        verifyNoInteractions(findSaleLineTicketPort);
    }

    @Test
    void saleLineTicketsWithAllRelations_listVariant_shouldLeaveNull_whenMissing() {
        // Arrange
        var line1 = makeLine(LINE_ID_1);
        var line2 = makeLine(LINE_ID_2);

        var view1 = mockTicketView(LINE_ID_1); // Falta el de LINE_ID_2
        when(findTicketPort.findAllBySaleLineTicketIds(anyList())).thenReturn(List.of(view1));

        // Act
        var result = factory.saleLineTicketsWithAllRelations(List.of(line1, line2));

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTicketView()).isEqualTo(view1);
        assertThat(result.get(1).getTicketView()).isNull();
        verify(findTicketPort).findAllBySaleLineTicketIds(anyList());
        verifyNoMoreInteractions(findTicketPort);
        verifyNoInteractions(findSaleLineTicketPort);
    }
}