package com.sap.sales_service.sale.infrastructure.input.kafka.listener;

import com.sap.common_lib.dto.response.sales.events.ResponseTicketEventDTO;
import com.sap.common_lib.dto.response.sales.events.UpdateStateSaleEventDTO;
import com.sap.common_lib.events.groups.GroupsConstants;
import com.sap.common_lib.events.topics.TopicConstants;
import com.sap.sales_service.sale.application.input.UpdateStateSaleCasePort;
import com.sap.sales_service.sale.application.input.UpdateTicketStateSalePort;
import com.sap.sales_service.sale.application.usecases.updatestatesale.dtos.UpdateStateSaleDTO;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class KafkaSaleEventListener {

    private final UpdateStateSaleCasePort updateStateSaleCasePort;
    private final UpdateTicketStateSalePort updateTicketStateSalePort;

    @KafkaListener(
            topics = TopicConstants.UPDATE_PAID_STATUS_SALE_TOPIC,
            groupId = GroupsConstants.SALES_SERVICE_GROUP_ID
    )
    public void onUpdatePaidStatusSaleEvent(@Payload UpdateStateSaleEventDTO updateStateSaleEventDTO) {

        var appDto = new UpdateStateSaleDTO(
                updateStateSaleEventDTO.saleId(),
                updateStateSaleEventDTO.paid(),
                updateStateSaleEventDTO.message()
        );
        updateStateSaleCasePort.updateStateSale(appDto);
    }

    @KafkaListener(
            topics = TopicConstants.RESPONSE_TICKET_SALES_TOPIC,
            groupId = GroupsConstants.SALES_SERVICE_GROUP_ID
    )
    public void onUpdateTicketStateSaleEvent(@Payload ResponseTicketEventDTO responseTicketEventDTO) {
        updateTicketStateSalePort.updateTicketState(
                responseTicketEventDTO.saleLineTicketId(),
                responseTicketEventDTO.status(),
                responseTicketEventDTO.message()
        );
    }
}
