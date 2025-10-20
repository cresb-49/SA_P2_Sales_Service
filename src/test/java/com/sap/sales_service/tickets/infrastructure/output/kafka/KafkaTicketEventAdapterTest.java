

package com.sap.sales_service.tickets.infrastructure.output.kafka;

import com.sap.common_lib.common.enums.sale.TicketStatusType;
import com.sap.common_lib.dto.response.sales.events.ResponseTicketEventDTO;
import com.sap.common_lib.events.topics.TopicConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaTicketEventAdapterTest {

    private static final UUID SALE_LINE_TICKET_ID = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
    private static final TicketStatusType STATUS_RESERVED = TicketStatusType.RESERVED;
    private static final TicketStatusType STATUS_IN_USE = TicketStatusType.IN_USE;
    private static final String MESSAGE_OK = "ok";
    private static final String TOPIC = TopicConstants.RESPONSE_TICKET_SALES_TOPIC;

    @Mock
    private KafkaTemplate<String, ResponseTicketEventDTO> kafkaResponseTicketTemplate;

    @InjectMocks
    private KafkaTicketEventAdapter adapter;

    @Test
    void respondSaleLineTicket_shouldPublishOnCorrectTopic_withKeyAndEvent() {
        // Arrange

        // Act
        adapter.respondSaleLineTicket(SALE_LINE_TICKET_ID, STATUS_RESERVED, MESSAGE_OK);

        // Assert
        ArgumentCaptor<ResponseTicketEventDTO> eventCaptor = ArgumentCaptor.forClass(ResponseTicketEventDTO.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaResponseTicketTemplate).send(eq(TOPIC), keyCaptor.capture(), eventCaptor.capture());

        ResponseTicketEventDTO sent = eventCaptor.getValue();
        assertThat(keyCaptor.getValue()).isEqualTo(SALE_LINE_TICKET_ID.toString());
        assertThat(sent.saleLineTicketId()).isEqualTo(SALE_LINE_TICKET_ID);
        assertThat(sent.status()).isEqualTo(STATUS_RESERVED);
        assertThat(sent.message()).isEqualTo(MESSAGE_OK);
    }

    @Test
    void respondSaleLineTicket_shouldAllowNullMessage_andStillPublish() {
        // Arrange

        // Act
        adapter.respondSaleLineTicket(SALE_LINE_TICKET_ID, STATUS_IN_USE, null);

        // Assert
        ArgumentCaptor<ResponseTicketEventDTO> eventCaptor = ArgumentCaptor.forClass(ResponseTicketEventDTO.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaResponseTicketTemplate).send(eq(TOPIC), keyCaptor.capture(), eventCaptor.capture());

        ResponseTicketEventDTO sent = eventCaptor.getValue();
        assertThat(keyCaptor.getValue()).isEqualTo(SALE_LINE_TICKET_ID.toString());
        assertThat(sent.saleLineTicketId()).isEqualTo(SALE_LINE_TICKET_ID);
        assertThat(sent.status()).isEqualTo(STATUS_IN_USE);
        assertThat(sent.message()).isNull();
    }

    @Test
    void respondSaleLineTicket_shouldPublishDifferentStatuses() {
        // Arrange
        TicketStatusType status = TicketStatusType.PURCHASED;

        // Act
        adapter.respondSaleLineTicket(SALE_LINE_TICKET_ID, status, MESSAGE_OK);

        // Assert
        ArgumentCaptor<ResponseTicketEventDTO> eventCaptor = ArgumentCaptor.forClass(ResponseTicketEventDTO.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaResponseTicketTemplate).send(eq(TOPIC), keyCaptor.capture(), eventCaptor.capture());

        ResponseTicketEventDTO sent = eventCaptor.getValue();
        assertThat(sent.status()).isEqualTo(status);
        assertThat(keyCaptor.getValue()).isEqualTo(SALE_LINE_TICKET_ID.toString());
    }
}