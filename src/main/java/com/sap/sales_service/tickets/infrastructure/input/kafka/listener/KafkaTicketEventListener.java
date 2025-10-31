package com.sap.sales_service.tickets.infrastructure.input.kafka.listener;

import com.sap.common_lib.dto.response.sales.events.CreateTicketEventDTO;
import com.sap.common_lib.events.groups.GroupsConstants;
import com.sap.common_lib.events.topics.TopicConstants;
import com.sap.sales_service.tickets.application.input.CreateTicketPort;
import com.sap.sales_service.tickets.application.usecases.createticket.dtos.CreateTicketDTO;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class KafkaTicketEventListener {

    private final CreateTicketPort createTicketPort;

    @KafkaListener(
            topics = TopicConstants.REQUEST_TICKET_SALES_TOPIC,
            groupId = GroupsConstants.SALES_SERVICE_GROUP_ID
    )
    public void onRequestTicketEvent(@Payload CreateTicketEventDTO createTicketEventDTO){
        var appDto = new CreateTicketDTO(
                createTicketEventDTO.saleLineTicketId(),
                createTicketEventDTO.cinemaFunctionId(),
                createTicketEventDTO.cinemaId(),
                createTicketEventDTO.cinemaRoomId(),
                createTicketEventDTO.movieId()
        );
        System.out.println("Received Kafka message: " + appDto.toString());
        createTicketPort.createTicket(appDto);
    }
}
