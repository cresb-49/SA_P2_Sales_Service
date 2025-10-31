package com.sap.sales_service.tickets.application.usecases.getoccupiedsetsbycinemafunction;

import com.sap.common_lib.common.enums.sale.TicketStatusType;
import com.sap.sales_service.tickets.application.output.FindingByFilterPort;
import com.sap.sales_service.tickets.domain.Ticket;
import com.sap.sales_service.tickets.domain.TicketFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetOccupiedSetsByCinemaFunctionTest {

    private static final UUID CINEMA_FUNCTION_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID ANY_SALE_LINE_TICKET_ID = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    private static final UUID ANY_CINEMA_ID = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
    private static final UUID ANY_ROOM_ID = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");
    private static final UUID ANY_MOVIE_ID = UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee");

    @Mock
    private FindingByFilterPort findingByFilterPort;

    private GetOccupiedSetsByCinemaFunction useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetOccupiedSetsByCinemaFunction(findingByFilterPort);
    }

    private static Ticket ticket() {
        return new Ticket(
                ANY_SALE_LINE_TICKET_ID,
                CINEMA_FUNCTION_ID,
                ANY_CINEMA_ID,
                ANY_ROOM_ID,
                ANY_MOVIE_ID
        );
    }

    private static ArgumentMatcher<TicketFilter> byStatusAndFunction(TicketStatusType status) {
        return f -> f != null
                && CINEMA_FUNCTION_ID.equals(f.cinemaFunctionId())
                && status == f.ticketStatus();
    }

    @Test
    void getOccupiedSeatsByCinemaFunctionId_shouldReturnZero_whenIdIsNull() {
        // Arrange

        // Act
        var result = useCase.getOccupiedSeatsByCinemaFunctionId(null);

        // Assert
        assertThat(result).isEqualTo(0);
        verifyNoInteractions(findingByFilterPort);
    }

    @Test
    void getOccupiedSeatsByCinemaFunctionId_shouldCountReservedSeats_only() {
        // Arrange
        var reserved = List.of(ticket(), ticket());
        given(findingByFilterPort.findByFilter(argThat(byStatusAndFunction(TicketStatusType.RESERVED))))
                .willReturn(reserved);
        given(findingByFilterPort.findByFilter(argThat(byStatusAndFunction(TicketStatusType.PURCHASED))))
                .willReturn(List.of());

        // Act
        var result = useCase.getOccupiedSeatsByCinemaFunctionId(CINEMA_FUNCTION_ID);

        // Assert
        assertThat(result).isEqualTo(2);
        verify(findingByFilterPort, times(1))
                .findByFilter(argThat(byStatusAndFunction(TicketStatusType.RESERVED)));
        verify(findingByFilterPort, times(1))
                .findByFilter(argThat(byStatusAndFunction(TicketStatusType.PURCHASED)));
    }

    @Test
    void getOccupiedSeatsByCinemaFunctionId_shouldCountPurchasedSeats_only() {
        // Arrange
        var purchased = List.of(ticket());
        given(findingByFilterPort.findByFilter(argThat(byStatusAndFunction(TicketStatusType.RESERVED))))
                .willReturn(List.of());
        given(findingByFilterPort.findByFilter(argThat(byStatusAndFunction(TicketStatusType.PURCHASED))))
                .willReturn(purchased);

        // Act
        var result = useCase.getOccupiedSeatsByCinemaFunctionId(CINEMA_FUNCTION_ID);

        // Assert
        assertThat(result).isEqualTo(1);
        verify(findingByFilterPort, times(1))
                .findByFilter(argThat(byStatusAndFunction(TicketStatusType.RESERVED)));
        verify(findingByFilterPort, times(1))
                .findByFilter(argThat(byStatusAndFunction(TicketStatusType.PURCHASED)));
    }

    @Test
    void getOccupiedSeatsByCinemaFunctionId_shouldSumCounts_fromBothStatuses_withoutDeduplication() {
        // Arrange
        var reserved = List.of(ticket(), ticket());
        var purchased = List.of(ticket(), ticket());
        given(findingByFilterPort.findByFilter(argThat(byStatusAndFunction(TicketStatusType.RESERVED))))
                .willReturn(reserved);
        given(findingByFilterPort.findByFilter(argThat(byStatusAndFunction(TicketStatusType.PURCHASED))))
                .willReturn(purchased);

        // Act
        var result = useCase.getOccupiedSeatsByCinemaFunctionId(CINEMA_FUNCTION_ID);

        // Assert
        assertThat(result).isEqualTo(4);
        verify(findingByFilterPort, times(1))
                .findByFilter(argThat(byStatusAndFunction(TicketStatusType.RESERVED)));
        verify(findingByFilterPort, times(1))
                .findByFilter(argThat(byStatusAndFunction(TicketStatusType.PURCHASED)));
    }

    @Test
    void getOccupiedSeatsByCinemaFunctionId_shouldReturnZero_whenNoTicketsFound() {
        // Arrange
        given(findingByFilterPort.findByFilter(argThat(byStatusAndFunction(TicketStatusType.RESERVED))))
                .willReturn(List.of());
        given(findingByFilterPort.findByFilter(argThat(byStatusAndFunction(TicketStatusType.PURCHASED))))
                .willReturn(List.of());

        // Act
        var result = useCase.getOccupiedSeatsByCinemaFunctionId(CINEMA_FUNCTION_ID);

        // Assert
        assertThat(result).isEqualTo(0);
        verify(findingByFilterPort, times(1))
                .findByFilter(argThat(byStatusAndFunction(TicketStatusType.RESERVED)));
        verify(findingByFilterPort, times(1))
                .findByFilter(argThat(byStatusAndFunction(TicketStatusType.PURCHASED)));
    }
}
