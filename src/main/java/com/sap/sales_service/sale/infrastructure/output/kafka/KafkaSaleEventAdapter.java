package com.sap.sales_service.sale.infrastructure.output.kafka;

import com.sap.common_lib.events.topics.TopicConstants;
import com.sap.sales_service.sale.application.ouput.SendNotificationPort;
import com.sap.sales_service.sale.application.ouput.SendTicketRequestPort;
import com.sap.sales_service.sale.domain.dtos.events.CreateTicketEventDTO;
import com.sap.sales_service.sale.domain.dtos.events.NotificationDTO;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class KafkaSaleEventAdapter implements SendTicketRequestPort, SendNotificationPort {

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
    public void sendNotification(NotificationDTO notificationDTO) {
        System.out.println("Sending notification: " + notificationDTO.toString());
    }
}
