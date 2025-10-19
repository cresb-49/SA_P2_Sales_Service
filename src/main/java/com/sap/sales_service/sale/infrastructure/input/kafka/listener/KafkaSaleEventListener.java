package com.sap.sales_service.sale.infrastructure.input.kafka.listener;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class KafkaSaleEventListener {

//    private final CreateTicketPort createTicketPort;
//
//    @KafkaListener(
//            topics = TopicConstants.REQUEST_TICKET_SALES_TOPIC,
//            groupId = GroupsConstants.SALES_SERVICE_GROUP_ID
//    )
//    public void onRequestTicketEvent(@Payload CreateTicketEventDTO createTicketEventDTO){
//        var appDto = new CreateTicketDTO(
//                createTicketEventDTO.saleLineTicketId(),
//                createTicketEventDTO.cinemaFunctionId(),
//                createTicketEventDTO.cinemaId(),
//                createTicketEventDTO.cinemaRoomId(),
//                createTicketEventDTO.seatId(),
//                createTicketEventDTO.movieId()
//        );
//        System.out.println("Received Kafka message: " + appDto.toString());
//        createTicketPort.createTicket(appDto);
//    }
}
