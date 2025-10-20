

package com.sap.sales_service.sale.application.factory;

import com.sap.sales_service.sale.domain.Sale;
import com.sap.sales_service.sale.domain.SaleLineSnack;
import com.sap.sales_service.sale.domain.SaleLineTicket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class SaleFactoryTest {

    private static final UUID SALE_ID_1 = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID SALE_ID_2 = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");

    private static final LocalDateTime NOW = LocalDateTime.of(2025, 1, 1, 12, 0);
    private static final BigDecimal ZERO = BigDecimal.ZERO;

    @Mock
    private SaleLineTicketFactory saleLineTicketFactory;

    @Mock
    private SaleLineSnackFactory saleLineSnackFactory;

    @InjectMocks
    private SaleFactory saleFactory;

    private Sale baseSale(UUID id) {
        return new Sale(
                id,
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                new BigDecimal("100.00"),
                ZERO,
                ZERO,
                com.sap.common_lib.common.enums.sale.SaleStatusType.PENDING,
                NOW,
                NOW,
                null
        );
    }

    private List<SaleLineTicket> ticketsFor(UUID saleId) {
        var t1 = new SaleLineTicket(
                UUID.randomUUID(), saleId, 1, new BigDecimal("10.00"),
                new BigDecimal("10.00"),
                com.sap.common_lib.common.enums.sale.TicketStatusType.PENDING,
                NOW, NOW
        );
        return List.of(t1);
    }

    private List<SaleLineSnack> snacksFor(UUID saleId) {
        var s1 = new SaleLineSnack(
                UUID.randomUUID(), saleId,
                UUID.fromString("33333333-3333-3333-3333-333333333333"),
                2, new BigDecimal("5.00"), new BigDecimal("10.00")
        );
        return List.of(s1);
    }

    @BeforeEach
    void setUp() {
        // Arrange
    }

    @Test
    void saleWithAllRelations_shouldAttachLinesFromFactories() {
        // Arrange
        var sale = baseSale(SALE_ID_1);
        var expectedTickets = ticketsFor(SALE_ID_1);
        var expectedSnacks = snacksFor(SALE_ID_1);
        when(saleLineTicketFactory.saleLineTicketWithAllRelations(SALE_ID_1)).thenReturn(expectedTickets);
        when(saleLineSnackFactory.saleLineSnackWithAllRelations(SALE_ID_1)).thenReturn(expectedSnacks);

        // Act
        var result = saleFactory.saleWithAllRelations(sale);

        // Assert
        assertThat(result).isNotSameAs(sale);
        assertThat(result.getSaleLineTickets()).containsExactlyElementsOf(expectedTickets);
        assertThat(result.getSaleLineSnacks()).containsExactlyElementsOf(expectedSnacks);
        verify(saleLineTicketFactory, times(1)).saleLineTicketWithAllRelations(SALE_ID_1);
        verify(saleLineSnackFactory, times(1)).saleLineSnackWithAllRelations(SALE_ID_1);
    }

    @Test
    void salesWithAllRelations_onPage_shouldMapEach() {
        // Arrange
        var sale1 = baseSale(SALE_ID_1);
        var sale2 = baseSale(SALE_ID_2);
        var page = new PageImpl<>(List.of(sale1, sale2));

        var tickets1 = ticketsFor(SALE_ID_1);
        var snacks1 = snacksFor(SALE_ID_1);
        var tickets2 = ticketsFor(SALE_ID_2);
        var snacks2 = snacksFor(SALE_ID_2);

        when(saleLineTicketFactory.saleLineTicketWithAllRelations(SALE_ID_1)).thenReturn(tickets1);
        when(saleLineSnackFactory.saleLineSnackWithAllRelations(SALE_ID_1)).thenReturn(snacks1);
        when(saleLineTicketFactory.saleLineTicketWithAllRelations(SALE_ID_2)).thenReturn(tickets2);
        when(saleLineSnackFactory.saleLineSnackWithAllRelations(SALE_ID_2)).thenReturn(snacks2);

        // Act
        Page<Sale> result = saleFactory.salesWithAllRelations(page);

        // Assert
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getSaleLineTickets()).containsExactlyElementsOf(tickets1);
        assertThat(result.getContent().get(0).getSaleLineSnacks()).containsExactlyElementsOf(snacks1);
        assertThat(result.getContent().get(1).getSaleLineTickets()).containsExactlyElementsOf(tickets2);
        assertThat(result.getContent().get(1).getSaleLineSnacks()).containsExactlyElementsOf(snacks2);
        verify(saleLineTicketFactory, times(1)).saleLineTicketWithAllRelations(SALE_ID_1);
        verify(saleLineSnackFactory, times(1)).saleLineSnackWithAllRelations(SALE_ID_1);
        verify(saleLineTicketFactory, times(1)).saleLineTicketWithAllRelations(SALE_ID_2);
        verify(saleLineSnackFactory, times(1)).saleLineSnackWithAllRelations(SALE_ID_2);
    }

    @Test
    void salesWithAllRelations_onList_shouldMapEach() {
        // Arrange
        var sale1 = baseSale(SALE_ID_1);
        var sale2 = baseSale(SALE_ID_2);

        var tickets1 = ticketsFor(SALE_ID_1);
        var snacks1 = snacksFor(SALE_ID_1);
        var tickets2 = ticketsFor(SALE_ID_2);
        var snacks2 = snacksFor(SALE_ID_2);

        when(saleLineTicketFactory.saleLineTicketWithAllRelations(SALE_ID_1)).thenReturn(tickets1);
        when(saleLineSnackFactory.saleLineSnackWithAllRelations(SALE_ID_1)).thenReturn(snacks1);
        when(saleLineTicketFactory.saleLineTicketWithAllRelations(SALE_ID_2)).thenReturn(tickets2);
        when(saleLineSnackFactory.saleLineSnackWithAllRelations(SALE_ID_2)).thenReturn(snacks2);

        // Act
        var result = saleFactory.salesWithAllRelations(List.of(sale1, sale2));

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSaleLineTickets()).containsExactlyElementsOf(tickets1);
        assertThat(result.get(0).getSaleLineSnacks()).containsExactlyElementsOf(snacks1);
        assertThat(result.get(1).getSaleLineTickets()).containsExactlyElementsOf(tickets2);
        assertThat(result.get(1).getSaleLineSnacks()).containsExactlyElementsOf(snacks2);
        verify(saleLineTicketFactory, times(1)).saleLineTicketWithAllRelations(SALE_ID_1);
        verify(saleLineSnackFactory, times(1)).saleLineSnackWithAllRelations(SALE_ID_1);
        verify(saleLineTicketFactory, times(1)).saleLineTicketWithAllRelations(SALE_ID_2);
        verify(saleLineSnackFactory, times(1)).saleLineSnackWithAllRelations(SALE_ID_2);
    }
}