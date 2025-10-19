package com.sap.sales_service.sale.infrastructure.output.kafka;

import com.sap.common_lib.events.topics.TopicConstants;
import com.sap.sales_service.sale.application.ouput.RefoundAmountRequestPort;
import com.sap.sales_service.sale.application.ouput.SendNotificationPort;
import com.sap.sales_service.sale.application.ouput.SendPaidRequestPort;
import com.sap.sales_service.sale.application.ouput.SendTicketRequestPort;
import com.sap.sales_service.sale.domain.dtos.events.CreateTicketEventDTO;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@AllArgsConstructor
public class KafkaSaleEventAdapter implements SendTicketRequestPort, SendNotificationPort, SendPaidRequestPort, RefoundAmountRequestPort {

    private final KafkaTemplate<String, com.sap.common_lib.dto.response.sales.events.CreateTicketEventDTO> createTicketEventDTOKafkaTemplate;

    @Override
    public void sendTicketRequest(CreateTicketEventDTO createTicketEventDTO) {
        var interfaceDTO = new com.sap.common_lib.dto.response.sales.events.CreateTicketEventDTO(
                createTicketEventDTO.saleLineTicketId(),
                createTicketEventDTO.cinemaFunctionId(),
                createTicketEventDTO.cinemaId(),
                createTicketEventDTO.cinemaRoomId(),
                createTicketEventDTO.seatId(),
                createTicketEventDTO.movieId()
        );
        createTicketEventDTOKafkaTemplate.send(TopicConstants.REQUEST_TICKET_SALES_TOPIC, interfaceDTO);
    }

    @Override
    public void sendPaidRequest(UUID saleId, BigDecimal amount) {
        System.out.println("Sending paid request for saleId: " + saleId + " with amount: " + amount);
    }

    @Override
    public void requestRefoundAmount(BigDecimal amount, UUID customerId, String message) {
        System.out.println("Requesting refound amount: " + amount + " for customerId: " + customerId + " with message: " + message);
    }

    @Override
    public void sendNotification(UUID userId, String message) {
        System.out.println("Sending notification to userId: " + userId + " with message: " + message);
    }
}
