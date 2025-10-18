package com.sap.sales_service.tickets.application.input;

import com.sap.sales_service.tickets.domain.Ticket;

import java.util.UUID;

public interface MarkUsedTicketPort {
    void markTicketAsUsed(UUID ticketId);
}
