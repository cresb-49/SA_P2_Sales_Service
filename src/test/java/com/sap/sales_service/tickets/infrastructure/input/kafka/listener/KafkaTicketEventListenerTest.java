

package com.sap.sales_service.tickets.infrastructure.input.kafka.listener;

import com.sap.common_lib.dto.response.sales.events.CreateTicketEventDTO;
import com.sap.sales_service.tickets.application.input.CreateTicketPort;
import com.sap.sales_service.tickets.application.usecases.createticket.dtos.CreateTicketDTO;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class KafkaTicketEventListenerTest {

    private static final UUID SALE_LINE_TICKET_ID = UUID.randomUUID();
    private static final UUID CINEMA_FUNCTION_ID = UUID.randomUUID();
    private static final UUID CINEMA_ID = UUID.randomUUID();
    private static final UUID CINEMA_ROOM_ID = UUID.randomUUID();
    private static final UUID MOVIE_ID = UUID.randomUUID();

    @Test
    void onRequestTicketEvent_shouldMapAndDelegateToUseCase() {
        // Arrange
        CreateTicketPort createTicketPort = mock(CreateTicketPort.class);
        KafkaTicketEventListener listener = new KafkaTicketEventListener(createTicketPort);
        CreateTicketEventDTO event = new CreateTicketEventDTO(
                SALE_LINE_TICKET_ID,
                CINEMA_FUNCTION_ID,
                CINEMA_ID,
                CINEMA_ROOM_ID,
                MOVIE_ID
        );

        // Act
        listener.onRequestTicketEvent(event);

        // Assert
        ArgumentCaptor<CreateTicketDTO> captor = ArgumentCaptor.forClass(CreateTicketDTO.class);
        then(createTicketPort).should().createTicket(captor.capture());
        CreateTicketDTO dto = captor.getValue();
        assertThat(dto.saleLineTicketId()).isEqualTo(SALE_LINE_TICKET_ID);
        assertThat(dto.cinemaFunctionId()).isEqualTo(CINEMA_FUNCTION_ID);
        assertThat(dto.cinemaId()).isEqualTo(CINEMA_ID);
        assertThat(dto.cinemaRoomId()).isEqualTo(CINEMA_ROOM_ID);
        assertThat(dto.movieId()).isEqualTo(MOVIE_ID);
    }
}