package com.sap.sales_service.tickets.infrastructure.input.domain.gateway;

import com.sap.sales_service.tickets.application.output.FindingTicketPort;
import com.sap.sales_service.tickets.infrastructure.input.domain.dtos.TicketDomainView;
import com.sap.sales_service.tickets.infrastructure.input.domain.mapper.TicketDomainViewMapper;
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

    private final TicketDomainViewMapper ticketDomainViewMapper;

    @Override
    public List<TicketDomainView> findBySaleLineTicketId(List<UUID> saleLineTicketIds) {
        return findingTicketPort.findBySaleLineTicketIds(saleLineTicketIds).stream()
                .map(ticketDomainViewMapper::toDomainView)
                .toList();
    }

    @Override
    public Optional<TicketDomainView> findBySaleLineTicketId(UUID saleLineTicketId) {
        return findingTicketPort.findBySaleLineTicketId(saleLineTicketId)
                .map(ticketDomainViewMapper::toDomainView);
    }

    @Override
    public Optional<TicketDomainView> findById(UUID id) {
        return findingTicketPort.findById(id)
                .map(ticketDomainViewMapper::toDomainView);
    }

    @Override
    public List<TicketDomainView> findByIds(List<UUID> ids) {
        return findingTicketPort.findByIds(ids).stream()
                .map(ticketDomainViewMapper::toDomainView)
                .toList();
    }
}
