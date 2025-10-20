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
    private static final UUID SEAT_1 = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID SEAT_2 = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID SEAT_3 = UUID.fromString("33333333-3333-3333-3333-333333333333");
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

    private static Ticket ticketWithSeat(UUID seatId) {
        return new Ticket(
                ANY_SALE_LINE_TICKET_ID,
                CINEMA_FUNCTION_ID,
                ANY_CINEMA_ID,
                ANY_ROOM_ID,
                seatId,
                ANY_MOVIE_ID
        );
    }

    private static ArgumentMatcher<TicketFilter> byStatusAndFunction(TicketStatusType status) {
        return f -> f != null
                && CINEMA_FUNCTION_ID.equals(f.cinemaFunctionId())
                && status == f.ticketStatus();
    }

    @Test
    void getOccupiedSeatsByCinemaFunctionId_shouldReturnEmpty_whenIdIsNull() {
        // Arrange

        // Act
        var result = useCase.getOccupiedSeatsByCinemaFunctionId(null);

        // Assert
        assertThat(result).isEmpty();
        verifyNoInteractions(findingByFilterPort);
    }

    @Test
    void getOccupiedSeatsByCinemaFunctionId_shouldReturnReservedSeats_only() {
        // Arrange
        var reserved = List.of(ticketWithSeat(SEAT_1), ticketWithSeat(SEAT_2));
        given(findingByFilterPort.findByFilter(argThat(byStatusAndFunction(TicketStatusType.RESERVED))))
                .willReturn(reserved);
        given(findingByFilterPort.findByFilter(argThat(byStatusAndFunction(TicketStatusType.PURCHASED))))
                .willReturn(List.of());

        // Act
        var result = useCase.getOccupiedSeatsByCinemaFunctionId(CINEMA_FUNCTION_ID);

        // Assert
        assertThat(result).containsExactlyInAnyOrder(SEAT_1, SEAT_2);
        verify(findingByFilterPort, times(1))
                .findByFilter(argThat(byStatusAndFunction(TicketStatusType.RESERVED)));
        verify(findingByFilterPort, times(1))
                .findByFilter(argThat(byStatusAndFunction(TicketStatusType.PURCHASED)));
    }

    @Test
    void getOccupiedSeatsByCinemaFunctionId_shouldReturnPurchasedSeats_only() {
        // Arrange
        var purchased = List.of(ticketWithSeat(SEAT_1));
        given(findingByFilterPort.findByFilter(argThat(byStatusAndFunction(TicketStatusType.RESERVED))))
                .willReturn(List.of());
        given(findingByFilterPort.findByFilter(argThat(byStatusAndFunction(TicketStatusType.PURCHASED))))
                .willReturn(purchased);

        // Act
        var result = useCase.getOccupiedSeatsByCinemaFunctionId(CINEMA_FUNCTION_ID);

        // Assert
        assertThat(result).containsExactly(SEAT_1);
        verify(findingByFilterPort, times(1))
                .findByFilter(argThat(byStatusAndFunction(TicketStatusType.RESERVED)));
        verify(findingByFilterPort, times(1))
                .findByFilter(argThat(byStatusAndFunction(TicketStatusType.PURCHASED)));
    }

    @Test
    void getOccupiedSeatsByCinemaFunctionId_shouldMergeAndDeduplicateSeats_fromBothStatuses() {
        // Arrange
        var reserved = List.of(ticketWithSeat(SEAT_1), ticketWithSeat(SEAT_2));
        var purchased = List.of(ticketWithSeat(SEAT_2), ticketWithSeat(SEAT_3));
        given(findingByFilterPort.findByFilter(argThat(byStatusAndFunction(TicketStatusType.RESERVED))))
                .willReturn(reserved);
        given(findingByFilterPort.findByFilter(argThat(byStatusAndFunction(TicketStatusType.PURCHASED))))
                .willReturn(purchased);

        // Act
        var result = useCase.getOccupiedSeatsByCinemaFunctionId(CINEMA_FUNCTION_ID);

        // Assert
        assertThat(result).containsExactlyInAnyOrder(SEAT_1, SEAT_2, SEAT_3);
        verify(findingByFilterPort, times(1))
                .findByFilter(argThat(byStatusAndFunction(TicketStatusType.RESERVED)));
        verify(findingByFilterPort, times(1))
                .findByFilter(argThat(byStatusAndFunction(TicketStatusType.PURCHASED)));
    }

    @Test
    void getOccupiedSeatsByCinemaFunctionId_shouldReturnEmpty_whenNoTicketsFound() {
        // Arrange
        given(findingByFilterPort.findByFilter(argThat(byStatusAndFunction(TicketStatusType.RESERVED))))
                .willReturn(List.of());
        given(findingByFilterPort.findByFilter(argThat(byStatusAndFunction(TicketStatusType.PURCHASED))))
                .willReturn(List.of());

        // Act
        var result = useCase.getOccupiedSeatsByCinemaFunctionId(CINEMA_FUNCTION_ID);

        // Assert
        assertThat(result).isEmpty();
        verify(findingByFilterPort, times(1))
                .findByFilter(argThat(byStatusAndFunction(TicketStatusType.RESERVED)));
        verify(findingByFilterPort, times(1))
                .findByFilter(argThat(byStatusAndFunction(TicketStatusType.PURCHASED)));
    }
}
