package com.sap.sales_service.tickets.application.input;

import com.sap.sales_service.tickets.domain.Ticket;

public interface FindTicketPort {
    Ticket findById(java.util.UUID id);
}
