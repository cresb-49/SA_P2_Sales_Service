package com.sap.sales_service.tickets.infrastructure.input.domain.gateway;

import com.sap.sales_service.tickets.application.output.FindingTicketPort;
import com.sap.sales_service.tickets.infrastructure.input.domain.dtos.TicketDomainView;
import com.sap.sales_service.tickets.infrastructure.input.domain.port.TicketGatewayPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class TicketGateway implements TicketGatewayPort {

    private final FindingTicketPort findingTicketPort;

    @Override
    public List<TicketDomainView> findBySaleLineTicketId(List<UUID> saleLineTicketIds) {
        return List.of();
    }

    @Override
    public Optional<TicketDomainView> findBySaleLineTicketId(UUID saleLineTicketId) {
        return Optional.empty();
    }

    @Override
    public Optional<TicketDomainView> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public List<TicketDomainView> findByIds(List<UUID> ids) {
        return List.of();
    }
}
