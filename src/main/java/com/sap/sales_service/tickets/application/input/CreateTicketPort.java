package com.sap.sales_service.tickets.application.input;

import com.sap.sales_service.tickets.application.usecases.createticket.dtos.CreateTicketDTO;
import com.sap.sales_service.tickets.domain.Ticket;

public interface CreateTicketPort {
    Ticket createTicket(CreateTicketDTO createTicketDTO);
}
