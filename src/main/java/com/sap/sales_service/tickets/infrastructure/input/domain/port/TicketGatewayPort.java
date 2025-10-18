package com.sap.sales_service.tickets.infrastructure.input.domain.port;

import com.sap.sales_service.tickets.infrastructure.input.domain.dtos.TicketDomainView;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketGatewayPort {
    List<TicketDomainView> findBySaleLineTicketId(List<UUID> saleLineTicketIds);
    Optional<TicketDomainView> findBySaleLineTicketId(UUID saleLineTicketId);
    Optional<TicketDomainView> findById(UUID id);
    List<TicketDomainView> findByIds(List<UUID> ids);
}
