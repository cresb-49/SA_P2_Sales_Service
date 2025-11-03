package com.sap.sales_service.sale.application.ouput;

import com.sap.sales_service.sale.domain.dtos.events.CreateTicketInternalViewEventDTO;

public interface SendTicketRequestPort {
    void sendTicketRequest(CreateTicketInternalViewEventDTO createTicketInternalViewEventDTO);
}
