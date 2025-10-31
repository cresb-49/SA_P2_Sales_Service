package com.sap.sales_service.tickets.application.output;

import com.sap.sales_service.tickets.domain.Ticket;
import org.apache.kafka.common.quota.ClientQuotaAlteration;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FindingTicketPort {
    Optional<Ticket> findById(UUID id);
    Optional<Ticket> findBySaleLineTicketId(UUID saleLineTicketId);
    List<Ticket> findByIds(List<UUID> ids);
    List<Ticket> findBySaleLineTicketIds(List<UUID> saleLineTicketIds);
}
