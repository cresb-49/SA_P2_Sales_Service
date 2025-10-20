package com.sap.sales_service.tickets.infrastructure.input.domain.gateway;

import com.sap.sales_service.tickets.application.output.FindingTicketPort;
import com.sap.sales_service.tickets.domain.Ticket;
import com.sap.sales_service.tickets.infrastructure.input.domain.dtos.TicketDomainView;
import com.sap.sales_service.tickets.infrastructure.input.domain.mapper.TicketDomainViewMapper;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TicketGatewayTest {

    private static final UUID SLT_ID_1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID SLT_ID_2 = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID TICKET_ID_1 = UUID.fromString("10000000-0000-0000-0000-000000000001");
    private static final UUID TICKET_ID_2 = UUID.fromString("10000000-0000-0000-0000-000000000002");
    private static final UUID CINEMA_FUNCTION_ID = UUID.fromString("20000000-0000-0000-0000-000000000001");
    private static final UUID CINEMA_ID = UUID.fromString("30000000-0000-0000-0000-000000000001");
    private static final UUID ROOM_ID = UUID.fromString("40000000-0000-0000-0000-000000000001");
    private static final UUID SEAT_ID = UUID.fromString("50000000-0000-0000-0000-000000000001");
    private static final UUID MOVIE_ID = UUID.fromString("60000000-0000-0000-0000-000000000001");

    @Mock private FindingTicketPort findingTicketPort;
    @Mock private TicketDomainViewMapper ticketDomainViewMapper;
    @InjectMocks private TicketGateway gateway;

    private void stubMapper() {
        given(ticketDomainViewMapper.toDomainView(any(Ticket.class))).willAnswer(inv -> {
            Ticket t = inv.getArgument(0);
            return new TicketDomainView(
                    t.getId(),
                    t.getSaleLineTicketId(),
                    t.getCinemaFunctionId(),
                    t.getCinemaId(),
                    t.getCinemaRoomId(),
                    t.getSeatId(),
                    t.getMovieId(),
                    t.isUsed(),
                    t.getCreatedAt(),
                    t.getUpdatedAt()
            );
        });
    }

    @Test
    void findBySaleLineTicketId_list_shouldMapAll() {
        // Arrange
        stubMapper();
        Ticket t1 = ticketWithFixedId(TICKET_ID_1, SLT_ID_1);
        Ticket t2 = ticketWithFixedId(TICKET_ID_2, SLT_ID_2);
        given(findingTicketPort.findBySaleLineTicketIds(List.of(SLT_ID_1, SLT_ID_2))).willReturn(List.of(t1, t2));
        // Act
        var result = gateway.findBySaleLineTicketId(List.of(SLT_ID_1, SLT_ID_2));
        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.stream().map(TicketDomainView::id)).containsExactlyInAnyOrder(TICKET_ID_1, TICKET_ID_2);
        assertThat(result.stream().map(TicketDomainView::saleLineTicketId)).containsExactlyInAnyOrder(SLT_ID_1, SLT_ID_2);
    }

    @Test
    void findBySaleLineTicketId_single_shouldReturnMappedOptional_whenFound() {
        // Arrange
        stubMapper();
        Ticket t1 = ticketWithFixedId(TICKET_ID_1, SLT_ID_1);
        given(findingTicketPort.findBySaleLineTicketId(SLT_ID_1)).willReturn(Optional.of(t1));
        // Act
        var result = gateway.findBySaleLineTicketId(SLT_ID_1);
        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(TICKET_ID_1);
        assertThat(result.get().saleLineTicketId()).isEqualTo(SLT_ID_1);
    }

    @Test
    void findBySaleLineTicketId_single_shouldReturnEmpty_whenNotFound() {
        // Arrange
        given(findingTicketPort.findBySaleLineTicketId(SLT_ID_1)).willReturn(Optional.empty());
        // Act
        var result = gateway.findBySaleLineTicketId(SLT_ID_1);
        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findById_shouldReturnMappedOptional_whenFound() {
        // Arrange
        stubMapper();
        Ticket t1 = ticketWithFixedId(TICKET_ID_1, SLT_ID_1);
        given(findingTicketPort.findById(TICKET_ID_1)).willReturn(Optional.of(t1));
        // Act
        var result = gateway.findById(TICKET_ID_1);
        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(TICKET_ID_1);
    }

    @Test
    void findById_shouldReturnEmpty_whenNotFound() {
        // Arrange
        given(findingTicketPort.findById(TICKET_ID_1)).willReturn(Optional.empty());
        // Act
        var result = gateway.findById(TICKET_ID_1);
        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findByIds_shouldMapAll() {
        // Arrange
        stubMapper();
        Ticket t1 = ticketWithFixedId(TICKET_ID_1, SLT_ID_1);
        Ticket t2 = ticketWithFixedId(TICKET_ID_2, SLT_ID_2);
        given(findingTicketPort.findByIds(List.of(TICKET_ID_1, TICKET_ID_2))).willReturn(List.of(t1, t2));
        // Act
        var result = gateway.findByIds(List.of(TICKET_ID_1, TICKET_ID_2));
        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.stream().map(TicketDomainView::id)).containsExactlyInAnyOrder(TICKET_ID_1, TICKET_ID_2);
    }

    @Test
    void findBySaleLineTicketId_list_shouldReturnEmpty_whenNoMatches() {
        // Arrange
        given(findingTicketPort.findBySaleLineTicketIds(List.of())).willReturn(List.of());
        // Act
        var result = gateway.findBySaleLineTicketId(List.of());
        // Assert
        assertThat(result).isEmpty();
    }

    private Ticket ticketWithFixedId(UUID fixedId, UUID saleLineTicketId) {
        Ticket t = new Ticket(
                saleLineTicketId,
                CINEMA_FUNCTION_ID,
                CINEMA_ID,
                ROOM_ID,
                SEAT_ID,
                MOVIE_ID
        );
        return new Ticket(
                fixedId,
                t.getSaleLineTicketId(),
                t.getCinemaFunctionId(),
                t.getCinemaId(),
                t.getCinemaRoomId(),
                t.getSeatId(),
                t.getMovieId(),
                t.isUsed(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
