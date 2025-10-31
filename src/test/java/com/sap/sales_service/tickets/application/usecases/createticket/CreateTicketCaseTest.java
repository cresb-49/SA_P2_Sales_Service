package com.sap.sales_service.tickets.application.usecases.createticket;

import com.sap.common_lib.common.enums.sale.TicketStatusType;
import com.sap.common_lib.exception.NonRetryableBusinessException;
import com.sap.sales_service.tickets.application.output.FindingTicketPort;
import com.sap.sales_service.tickets.application.output.ResponseSaleLineTicketPort;
import com.sap.sales_service.tickets.application.output.SaveTicketPort;
import com.sap.sales_service.tickets.application.usecases.createticket.dtos.CreateTicketDTO;
import com.sap.sales_service.tickets.domain.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @InjectMocks
    private CreateTicketCase createTicketCase;

    @Captor
    private ArgumentCaptor<Ticket> ticketCaptor;

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
}
