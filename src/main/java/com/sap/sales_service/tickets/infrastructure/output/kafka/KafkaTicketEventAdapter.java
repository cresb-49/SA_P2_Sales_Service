package com.sap.sales_service.tickets.infrastructure.output.kafka;

import com.sap.common_lib.common.enums.sale.TicketStatusType;
import com.sap.common_lib.dto.response.sales.events.ResponseTicketEventDTO;
import com.sap.common_lib.events.topics.TopicConstants;
import com.sap.sales_service.tickets.application.output.ResponseSaleLineTicketPort;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class KafkaTicketEventAdapter implements ResponseSaleLineTicketPort {

    private final KafkaTemplate<String, ResponseTicketEventDTO> kafkaResponseTicketTemplate;

    @Override
    public void respondSaleLineTicket(UUID saleLineTicketId, TicketStatusType status, String message) {
        var event = new ResponseTicketEventDTO(saleLineTicketId, status, message);
        kafkaResponseTicketTemplate.send(TopicConstants.RESPONSE_TICKET_SALES_TOPIC, event.saleLineTicketId().toString(), event);
    }
}
