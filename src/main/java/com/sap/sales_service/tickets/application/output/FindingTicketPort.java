package com.sap.sales_service.tickets.application.output;

import com.sap.sales_service.tickets.domain.Ticket;

import java.util.Optional;
import java.util.UUID;

public interface FindingTicketPort {
    Optional<Ticket> findById(UUID id);

}
