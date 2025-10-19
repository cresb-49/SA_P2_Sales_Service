package com.sap.sales_service.sale.application.ouput;

import com.sap.sales_service.sale.domain.dtos.events.CreateTicketEventDTO;

public interface SendTicketRequestPort {
    void sendTicketRequest(CreateTicketEventDTO createTicketEventDTO);
}
