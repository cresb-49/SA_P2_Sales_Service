package com.sap.sales_service.tickets.application.usecases.createticket;

import com.sap.common_lib.common.enums.sale.TicketStatusType;
import com.sap.common_lib.exception.NonRetryableBusinessException;
import com.sap.sales_service.tickets.application.input.GetOccupiedSetsByCinemaFunctionPort;
import com.sap.sales_service.tickets.application.output.*;
import com.sap.sales_service.tickets.application.usecases.createticket.dtos.CreateTicketDTO;
import com.sap.sales_service.tickets.domain.Ticket;
import com.sap.sales_service.tickets.domain.dtos.CinemaView;
import com.sap.sales_service.tickets.domain.dtos.MovieView;
import com.sap.sales_service.tickets.domain.dtos.ShowtimeView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CreateTicketCaseTest {

    private static final UUID SALE_LINE_TICKET_ID = UUID.randomUUID();
    private static final UUID CINEMA_FUNCTION_ID = UUID.randomUUID();
    private static final UUID CINEMA_ID = UUID.randomUUID();
    private static final UUID CINEMA_ROOM_ID = UUID.randomUUID();
    private static final UUID SEAT_ID = UUID.randomUUID();
    private static final UUID MOVIE_ID = UUID.randomUUID();

    @Mock
    private FindingTicketPort findingTicketPort;

    @Mock
    private SaveTicketPort saveTicketPort;

    @Mock
    private ResponseSaleLineTicketPort responseSaleLineTicketPort;

    @Mock
    private GetOccupiedSetsByCinemaFunctionPort getOccupiedSetsByCinemaFunctionPort;

    @Mock
    private FindingShowtimePort findingShowtimePort;

    @Mock
    private FindingMoviePort findingMoviePort;

    @Mock
    private FindingCinemaPort findingCinemaPort;

    @InjectMocks
    private CreateTicketCase createTicketCase;

    private CreateTicketDTO dto;

    @BeforeEach
    void setUp() {
        dto = new CreateTicketDTO(
                SALE_LINE_TICKET_ID,
                CINEMA_FUNCTION_ID,
                CINEMA_ID,
                CINEMA_ROOM_ID,
                MOVIE_ID
        );
    }

    @Test
    void createTicket_shouldThrow_whenTicketAlreadyExistsForSaleLine() {
        // Arrange
        given(findingTicketPort.findBySaleLineTicketId(SALE_LINE_TICKET_ID))
                .willReturn(Optional.of(new Ticket(SALE_LINE_TICKET_ID, CINEMA_FUNCTION_ID, CINEMA_ID, CINEMA_ROOM_ID, MOVIE_ID)));

        // Act
        assertThrows(NonRetryableBusinessException.class, () -> createTicketCase.createTicket(dto));

        // Assert
        verify(saveTicketPort, never()).save(any(Ticket.class));
        verify(responseSaleLineTicketPort, never()).respondSaleLineTicket(any(), any(), anyString());
    }

    @Test
    void createTicket_shouldCreateTicketAndRespondReserved_whenSeatsAvailable() {
        // Arrange
        given(findingTicketPort.findBySaleLineTicketId(SALE_LINE_TICKET_ID)).willReturn(Optional.empty());
        given(findingMoviePort.findMovieById(MOVIE_ID)).willReturn(new MovieView("Matrix"));
        given(findingShowtimePort.findShowtimeById(CINEMA_FUNCTION_ID)).willReturn(
                new ShowtimeView(
                        LocalDateTime.of(2024, 1, 1, 18, 0),
                        LocalDateTime.of(2024, 1, 1, 20, 0),
                        100
                )
        );
        given(findingCinemaPort.findCinemaById(CINEMA_ID)).willReturn(new CinemaView("Cinema UX"));
        given(getOccupiedSetsByCinemaFunctionPort.getOccupiedSeatsByCinemaFunctionId(CINEMA_FUNCTION_ID)).willReturn(10);
        given(saveTicketPort.save(any(Ticket.class))).willAnswer(invocation -> invocation.getArgument(0));

        // Act
        Ticket result = createTicketCase.createTicket(dto);

        // Assert
        assertThat(result.getSaleLineTicketId()).isEqualTo(SALE_LINE_TICKET_ID);
        assertThat(result.getCinemaFunctionId()).isEqualTo(CINEMA_FUNCTION_ID);
        assertThat(result.getCinemaRoomId()).isEqualTo(CINEMA_ROOM_ID);
        assertThat(result.getMovieId()).isEqualTo(MOVIE_ID);

        verify(saveTicketPort).save(result);
        verify(responseSaleLineTicketPort).respondSaleLineTicket(
                eq(SALE_LINE_TICKET_ID),
                eq(TicketStatusType.RESERVED),
                argThat(message -> message.contains("ha sido creado y reservado exitosamente"))
        );
    }

    @Test
    void createTicket_shouldRespondAndThrow_whenNoSeatsAvailable() {
        // Arrange
        given(findingTicketPort.findBySaleLineTicketId(SALE_LINE_TICKET_ID)).willReturn(Optional.empty());
        given(findingMoviePort.findMovieById(MOVIE_ID)).willReturn(new MovieView("Matrix"));
        given(findingShowtimePort.findShowtimeById(CINEMA_FUNCTION_ID)).willReturn(
                new ShowtimeView(
                        LocalDateTime.of(2024, 1, 1, 18, 0),
                        LocalDateTime.of(2024, 1, 1, 20, 0),
                        50
                )
        );
        given(findingCinemaPort.findCinemaById(CINEMA_ID)).willReturn(new CinemaView("Cinema UX"));
        given(getOccupiedSetsByCinemaFunctionPort.getOccupiedSeatsByCinemaFunctionId(CINEMA_FUNCTION_ID)).willReturn(50);

        // Act & Assert
        NonRetryableBusinessException exception = assertThrows(
                NonRetryableBusinessException.class,
                () -> createTicketCase.createTicket(dto)
        );
        assertThat(exception.getMessage()).contains("No hay asientos disponibles");

        verify(responseSaleLineTicketPort).respondSaleLineTicket(
                eq(SALE_LINE_TICKET_ID),
                eq(TicketStatusType.IN_USE),
                argThat(message -> message.contains("No hay asientos disponibles"))
        );
        verify(saveTicketPort, never()).save(any());
    }
}
