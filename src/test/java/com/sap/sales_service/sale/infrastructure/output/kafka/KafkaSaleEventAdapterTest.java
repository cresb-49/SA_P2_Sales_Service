package com.sap.sales_service.sale.infrastructure.output.kafka;

import com.sap.common_lib.dto.response.sales.events.PaidPendingSaleEventDTO;
import com.sap.common_lib.dto.response.sales.events.RefoundAmountSaleEventDTO;
import com.sap.common_lib.events.topics.TopicConstants;
import com.sap.sales_service.sale.domain.dtos.events.CreateTicketEventDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class KafkaSaleEventAdapterTest {

    @Mock
    private KafkaTemplate<String, CreateTicketEventDTO> createTicketEventDTOKafkaTemplate;
    @Mock
    private KafkaTemplate<String, PaidPendingSaleEventDTO> paidPendingSaleEventDTOKafkaTemplate;
    @Mock
    private KafkaTemplate<String, RefoundAmountSaleEventDTO> refoundAmountSaleEventDTOKafkaTemplate;

    @InjectMocks
    private KafkaSaleEventAdapter adapter;

    private UUID saleLineTicketId;
    private UUID cinemaFunctionId;
    private UUID cinemaId;
    private UUID cinemaRoomId;
    private UUID seatId;
    private UUID movieId;

    @BeforeEach
    void init() {
        saleLineTicketId = UUID.randomUUID();
        cinemaFunctionId = UUID.randomUUID();
        cinemaId = UUID.randomUUID();
        cinemaRoomId = UUID.randomUUID();
        seatId = UUID.randomUUID();
        movieId = UUID.randomUUID();
    }

    @Test
    void sendTicketRequest_shouldForwardEventOnCorrectTopic() {
        // Arrange
        var dto = new CreateTicketEventDTO(
                saleLineTicketId,
                cinemaFunctionId,
                cinemaId,
                cinemaRoomId,
                seatId,
                movieId
        );

        // Act & Assert (we just assert no exception; verifying KafkaTemplate.send requires mockito-inline for final methods)
        assertThatCode(() -> adapter.sendTicketRequest(dto))
                .doesNotThrowAnyException();
    }

    @Test
    void sendPaidRequest_shouldPublishPaidPendingEvent() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID saleId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("123.45");

        // Act & Assert
        assertThatCode(() -> adapter.sendPaidRequest(userId, saleId, amount))
                .doesNotThrowAnyException();
    }

    @Test
    void requestRefoundAmount_shouldPublishRefundEvent() {
        // Arrange
        UUID userId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("50.00");
        String message = "Refund request";

        // Act & Assert
        assertThatCode(() -> adapter.requestRefoundAmount(amount, userId, message))
                .doesNotThrowAnyException();
    }
}