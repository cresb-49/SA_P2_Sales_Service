package com.sap.sales_service.sale.infrastructure.output.domain.adapter;

import com.sap.sales_service.sale.domain.dtos.TicketView;
import com.sap.sales_service.sale.infrastructure.output.domain.mapper.SaleTicketViewMapper;
import com.sap.sales_service.tickets.infrastructure.input.domain.dtos.TicketDomainView;
import com.sap.sales_service.tickets.infrastructure.input.domain.port.TicketGatewayPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class SaleTicketAdapterTest {

    @Mock
    private TicketGatewayPort ticketGatewayPort;
    @Mock
    private SaleTicketViewMapper ticketViewMapper;

    @InjectMocks
    private SaleTicketAdapter adapter;

    private UUID saleLineTicketId;
    private TicketDomainView domainView;
    private TicketView mappedView;

    @BeforeEach
    void setUp() {
        saleLineTicketId = UUID.randomUUID();
        var id = UUID.randomUUID();
        var cinemaFunctionId = UUID.randomUUID();
        var cinemaId = UUID.randomUUID();
        var cinemaRoomId = UUID.randomUUID();
        var movieId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var now = LocalDateTime.now();

        domainView = new TicketDomainView(
                id,
                saleLineTicketId,
                cinemaFunctionId,
                cinemaId,
                cinemaRoomId,
                movieId,
                Boolean.FALSE,
                now,
                now
        );

        mappedView = new TicketView(
                id,
                saleLineTicketId,
                cinemaFunctionId,
                cinemaId,
                cinemaRoomId,
                movieId,
                false,
                now,
                now
        );
    }

    @Test
    void findBySaleLineTicketId_shouldReturnMapped_whenFound() {
        // given
        given(ticketGatewayPort.findBySaleLineTicketId(saleLineTicketId))
                .willReturn(Optional.of(domainView));
        given(ticketViewMapper.toView(domainView)).willReturn(mappedView);

        // when
        var result = adapter.findBySaleLineTicketId(saleLineTicketId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(mappedView);
        verify(ticketGatewayPort).findBySaleLineTicketId(saleLineTicketId);
        verify(ticketViewMapper).toView(domainView);
    }

    @Test
    void findBySaleLineTicketId_shouldReturnEmpty_whenNotFound() {
        // given
        given(ticketGatewayPort.findBySaleLineTicketId(saleLineTicketId))
                .willReturn(Optional.empty());

        // when
        var result = adapter.findBySaleLineTicketId(saleLineTicketId);

        // then
        assertThat(result).isEmpty();
        verify(ticketGatewayPort).findBySaleLineTicketId(saleLineTicketId);
        verifyNoInteractions(ticketViewMapper);
    }

    @Test
    void findAllBySaleLineTicketIds_shouldMapEachDomain_toView() {
        // given
        var id2 = UUID.randomUUID();
        var cinemaFunctionId2 = UUID.randomUUID();
        var cinemaId2 = UUID.randomUUID();
        var cinemaRoomId2 = UUID.randomUUID();
        var movieId2 = UUID.randomUUID();
        var now2 = LocalDateTime.now();

        TicketDomainView domainView2 = new TicketDomainView(
                id2,
                saleLineTicketId,
                cinemaFunctionId2,
                cinemaId2,
                cinemaRoomId2,
                movieId2,
                Boolean.TRUE,
                now2,
                now2
        );

        TicketView mappedView2 = new TicketView(
                id2,
                saleLineTicketId,
                cinemaFunctionId2,
                cinemaId2,
                cinemaRoomId2,
                movieId2,
                true,
                now2,
                now2
        );

        List<UUID> ids = List.of(saleLineTicketId);
        List<TicketDomainView> domainList = List.of(domainView, domainView2);
        given(ticketGatewayPort.findBySaleLineTicketId(ids)).willReturn(domainList);
        given(ticketViewMapper.toView(domainView)).willReturn(mappedView);
        given(ticketViewMapper.toView(domainView2)).willReturn(mappedView2);

        // when
        var result = adapter.findAllBySaleLineTicketIds(ids);

        // then
        assertThat(result).containsExactly(mappedView, mappedView2);
        verify(ticketGatewayPort).findBySaleLineTicketId(ids);
    }
}