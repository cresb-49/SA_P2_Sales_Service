

package com.sap.sales_service.sale.infrastructure.input.kafka.listener;

import com.sap.common_lib.dto.response.notification.events.SendGenericMailEventDTO;
import com.sap.common_lib.dto.response.sales.events.PaidPendingSaleEventDTO;
import com.sap.common_lib.dto.response.sales.events.RefoundAmountSaleEventDTO;
import com.sap.common_lib.events.topics.TopicConstants;
import com.sap.sales_service.sale.domain.dtos.events.CreateTicketEventDTO;
import com.sap.sales_service.sale.infrastructure.output.kafka.KafkaSaleEventAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Despite the filename (historically for a listener), this test targets the
 * KafkaSaleEventAdapter which is responsible for producing Kafka events.
 */
class KafkaSaleEventListenerTest {

    @Mock
    private KafkaTemplate<String, CreateTicketEventDTO> createTicketTemplate;
    @Mock
    private KafkaTemplate<String, PaidPendingSaleEventDTO> paidPendingTemplate;
    @Mock
    private KafkaTemplate<String, RefoundAmountSaleEventDTO> refoundTemplate;
    @Mock
    private KafkaTemplate<String, SendGenericMailEventDTO> notificationTemplate;

    private KafkaSaleEventAdapter adapter;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        adapter = new KafkaSaleEventAdapter(createTicketTemplate, paidPendingTemplate, refoundTemplate, notificationTemplate);
    }

    @Test
    void sendTicketRequest_shouldSendOnCorrectTopic_withClonedPayload() {
        // Given
        UUID saleLineTicketId = UUID.randomUUID();
        UUID functionId = UUID.randomUUID();
        UUID cinemaId = UUID.randomUUID();
        UUID roomId = UUID.randomUUID();
        UUID seatId = UUID.randomUUID();
        UUID movieId = UUID.randomUUID();
        var dto = new CreateTicketEventDTO(saleLineTicketId, functionId, cinemaId, roomId, seatId, movieId);

        // When
        adapter.sendTicketRequest(dto);

        // Then
        ArgumentCaptor<String> topicCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<CreateTicketEventDTO> dtoCap = ArgumentCaptor.forClass(CreateTicketEventDTO.class);
        verify(createTicketTemplate, times(1)).send(topicCap.capture(), dtoCap.capture());

        assertThat(topicCap.getValue()).isEqualTo(TopicConstants.REQUEST_TICKET_SALES_TOPIC);
        var sent = dtoCap.getValue();
        assertThat(sent.saleLineTicketId()).isEqualTo(saleLineTicketId);
        assertThat(sent.cinemaFunctionId()).isEqualTo(functionId);
        assertThat(sent.cinemaId()).isEqualTo(cinemaId);
        assertThat(sent.cinemaRoomId()).isEqualTo(roomId);
        assertThat(sent.seatId()).isEqualTo(seatId);
        assertThat(sent.movieId()).isEqualTo(movieId);
        // Ensure a *new* instance was sent (adapter clones the payload)
        assertThat(sent).isNotSameAs(dto);
    }

    @Test
    void sendPaidRequest_shouldPublishPaidPendingSaleEvent_onCorrectTopic() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID saleId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("123.45");

        // When
        adapter.sendPaidRequest(userId, saleId, amount);

        // Then
        ArgumentCaptor<String> topicCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<PaidPendingSaleEventDTO> dtoCap = ArgumentCaptor.forClass(PaidPendingSaleEventDTO.class);
        verify(paidPendingTemplate).send(topicCap.capture(), dtoCap.capture());

        assertThat(topicCap.getValue()).isEqualTo(TopicConstants.SALES_PENDING_PAYMENT_TOPIC);
        var sent = dtoCap.getValue();
        assertThat(sent.customerId()).isEqualTo(userId);
        assertThat(sent.saleId()).isEqualTo(saleId);
        assertThat(sent.amount()).isEqualByComparingTo(amount);
    }

    @Test
    void requestRefoundAmount_shouldPublishRefundEvent_withCustomerAndAmount_onCorrectTopic() {
        // Given
        BigDecimal amount = new BigDecimal("50.00");
        UUID customerId = UUID.randomUUID();
        String message = "Reembolso por cancelación";

        // When
        adapter.requestRefoundAmount(amount, customerId, message);

        // Then
        ArgumentCaptor<String> topicCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<RefoundAmountSaleEventDTO> dtoCap = ArgumentCaptor.forClass(RefoundAmountSaleEventDTO.class);
        verify(refoundTemplate).send(topicCap.capture(), dtoCap.capture());

        assertThat(topicCap.getValue()).isEqualTo(TopicConstants.REFUND_AMOUNT_SALE_TOPIC);
        var sent = dtoCap.getValue();
        assertThat(sent.customerId()).isEqualTo(customerId);
        assertThat(sent.amount()).isEqualByComparingTo(amount);
        // The adapter generates a random correlation id; just assert it's present
        assertThat(sent.saleId()).isNotNull();
    }
}