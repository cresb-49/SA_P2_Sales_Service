package com.sap.sales_service.tickets.application.output;

import com.sap.sales_service.tickets.domain.Ticket;

public interface SaveTicketPort {
    Ticket save(Ticket ticket);
}
