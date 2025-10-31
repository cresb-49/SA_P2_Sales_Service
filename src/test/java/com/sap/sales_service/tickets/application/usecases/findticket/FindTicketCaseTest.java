

package com.sap.sales_service.tickets.application.usecases.findticket;

import com.sap.common_lib.exception.NotFoundException;
import com.sap.sales_service.tickets.application.output.FindingTicketPort;
import com.sap.sales_service.tickets.domain.Ticket;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FindTicketCaseTest {

    private static final UUID TICKET_ID = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
    private static final UUID SALE_LINE_TICKET_ID = UUID.fromString("11111111-2222-3333-4444-555555555555");
    private static final UUID FUNCTION_ID = UUID.fromString("66666666-7777-8888-9999-000000000000");
    private static final UUID CINEMA_ID = UUID.fromString("12345678-1234-1234-1234-1234567890ab");
    private static final UUID ROOM_ID = UUID.fromString("abcdefab-cdef-abcd-efab-cdefabcdefab");
    private static final UUID MOVIE_ID = UUID.fromString("deadbeef-dead-beef-dead-beefdeadbeef");

    @Mock
    private FindingTicketPort findingTicketPort;

    @InjectMocks
    private FindTicketCase findTicketCase;

    @Test
    void findById_shouldReturnTicket_whenFound() {
        // Arrange
        Ticket ticket = new Ticket(SALE_LINE_TICKET_ID, FUNCTION_ID, CINEMA_ID, ROOM_ID, MOVIE_ID);
        given(findingTicketPort.findById(TICKET_ID)).willReturn(Optional.of(ticket));

        // Act
        Ticket result = findTicketCase.findById(TICKET_ID);

        // Assert
        assertThat(result).isSameAs(ticket);
    }

    @Test
    void findById_shouldThrowNotFound_whenAbsent() {
        // Arrange
        given(findingTicketPort.findById(TICKET_ID)).willReturn(Optional.empty());

        // Act
        Throwable thrown = assertThrows(NotFoundException.class, () -> findTicketCase.findById(TICKET_ID));

        // Assert
        assertThat(thrown).isInstanceOf(NotFoundException.class);
    }
}