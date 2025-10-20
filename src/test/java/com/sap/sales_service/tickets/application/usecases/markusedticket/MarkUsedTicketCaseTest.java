

package com.sap.sales_service.tickets.application.usecases.markusedticket;

import com.sap.common_lib.common.enums.sale.SaleStatusType;
import com.sap.common_lib.common.enums.sale.TicketStatusType;
import com.sap.common_lib.exception.NotFoundException;
import com.sap.sales_service.tickets.application.output.FindingByFilterPort;
import com.sap.sales_service.tickets.application.output.FindingTicketPort;
import com.sap.sales_service.tickets.application.output.SaveTicketPort;
import com.sap.sales_service.tickets.domain.Ticket;
import com.sap.sales_service.tickets.domain.TicketFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarkUsedTicketCaseTest {

    // Arrange constants
    private static final UUID TICKET_ID = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
    private static final UUID OTHER_TICKET_ID = UUID.fromString("11111111-2222-3333-4444-555555555555");
    private static final UUID SALE_LINE_TICKET_ID = UUID.fromString("99999999-8888-7777-6666-555555555555");
    private static final UUID CINEMA_FUNCTION_ID = UUID.fromString("12345678-1234-1234-1234-123456789012");
    private static final UUID CINEMA_ID = UUID.fromString("aaaaaaaa-1111-2222-3333-bbbbbbbbbbbb");
    private static final UUID CINEMA_ROOM_ID = UUID.fromString("bbbbbbbb-2222-3333-4444-cccccccccccc");
    private static final UUID SEAT_ID = UUID.fromString("cccccccc-3333-4444-5555-dddddddddddd");
    private static final UUID MOVIE_ID = UUID.fromString("dddddddd-4444-5555-6666-eeeeeeeeeeee");

    @Mock private FindingTicketPort findingTicketPort;
    @Mock private FindingByFilterPort findingByFilterPort;
    @Mock private SaveTicketPort saveTicketPort;

    @InjectMocks private MarkUsedTicketCase useCase;

    private Ticket freshTicketWithId(UUID id) {
        return new Ticket(
                id,
                SALE_LINE_TICKET_ID,
                CINEMA_FUNCTION_ID,
                CINEMA_ID,
                CINEMA_ROOM_ID,
                SEAT_ID,
                MOVIE_ID,
                false,
                LocalDateTime.now().minusMinutes(1),
                LocalDateTime.now().minusSeconds(30)
        );
    }

    private Ticket freshTicket() {
        return freshTicketWithId(TICKET_ID);
    }

    @BeforeEach
    void setupCommon() {
        // No-op; per-test stubbing only
    }

    @Test
    @DisplayName("markTicketAsUsed throws NotFoundException when ticket does not exist")
    void markTicketAsUsed_shouldThrow_whenTicketNotFound() {
        // Arrange
        given(findingTicketPort.findById(TICKET_ID)).willReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> useCase.markTicketAsUsed(TICKET_ID))
                .isInstanceOf(NotFoundException.class);

        verify(findingByFilterPort, never()).findBySpecificIdAndFilter(any(TicketFilter.class), any(UUID.class));
        verify(saveTicketPort, never()).save(any());
    }

    @Test
    @DisplayName("markTicketAsUsed throws IllegalStateException when filter lookup does not return ticket")
    void markTicketAsUsed_shouldThrow_whenFilterDoesNotReturnTicket() {
        // Arrange
        given(findingTicketPort.findById(TICKET_ID)).willReturn(Optional.of(freshTicket()));
        given(findingByFilterPort.findBySpecificIdAndFilter(
                TicketFilter.builder()
                        .ticketStatus(TicketStatusType.PURCHASED)
                        .saleStatus(SaleStatusType.PAID)
                        .build(),
                TICKET_ID
        )).willReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> useCase.markTicketAsUsed(TICKET_ID))
                .isInstanceOf(IllegalStateException.class);

        verify(saveTicketPort, never()).save(any());
    }

    @Test
    @DisplayName("markTicketAsUsed throws RuntimeException when returned ticket has different ID reference")
    void markTicketAsUsed_shouldThrow_whenIdMismatch() {
        // Arrange
        given(findingTicketPort.findById(TICKET_ID)).willReturn(Optional.of(freshTicket()));
        var different = freshTicketWithId(OTHER_TICKET_ID);
        given(findingByFilterPort.findBySpecificIdAndFilter(
                TicketFilter.builder()
                        .ticketStatus(TicketStatusType.PURCHASED)
                        .saleStatus(SaleStatusType.PAID)
                        .build(),
                TICKET_ID
        )).willReturn(Optional.of(different));

        // Act + Assert
        assertThatThrownBy(() -> useCase.markTicketAsUsed(TICKET_ID))
                .isInstanceOf(RuntimeException.class);

        verify(saveTicketPort, never()).save(any());
    }

    @Test
    @DisplayName("markTicketAsUsed sets used=true and persists when found and valid")
    void markTicketAsUsed_shouldSetUsedAndSave_whenValid() {
        // Arrange
        var ticket = freshTicket();
        given(findingTicketPort.findById(TICKET_ID)).willReturn(Optional.of(ticket));
        given(findingByFilterPort.findBySpecificIdAndFilter(
                TicketFilter.builder()
                        .ticketStatus(TicketStatusType.PURCHASED)
                        .saleStatus(SaleStatusType.PAID)
                        .build(),
                TICKET_ID
        )).willReturn(Optional.of(ticket));

        // Act
        useCase.markTicketAsUsed(TICKET_ID);

        // Assert
        ArgumentCaptor<Ticket> captor = ArgumentCaptor.forClass(Ticket.class);
        verify(saveTicketPort, times(1)).save(captor.capture());
        assertThat(captor.getValue()).isNotNull();
        assertThat(captor.getValue().isUsed()).isTrue();
        assertThat(captor.getValue().getId()).isEqualTo(TICKET_ID);
    }

    @Test
    @DisplayName("markTicketAsUsed propagates domain error when ticket already used")
    void markTicketAsUsed_shouldPropagate_whenTicketAlreadyUsed() {
        // Arrange
        var usedTicket = freshTicket();
        usedTicket.markAsUsed(); // set to used already
        given(findingTicketPort.findById(TICKET_ID)).willReturn(Optional.of(usedTicket));
        given(findingByFilterPort.findBySpecificIdAndFilter(
                TicketFilter.builder()
                        .ticketStatus(TicketStatusType.PURCHASED)
                        .saleStatus(SaleStatusType.PAID)
                        .build(),
                TICKET_ID
        )).willReturn(Optional.of(usedTicket));

        // Act + Assert
        assertThatThrownBy(() -> useCase.markTicketAsUsed(TICKET_ID))
                .isInstanceOf(RuntimeException.class);

        verify(saveTicketPort, never()).save(any());
    }
}