package com.sap.sales_service.sale.infrastructure.output.domain.adapter;

import com.sap.sales_service.sale.application.ouput.FindTicketPort;
import com.sap.sales_service.sale.domain.dtos.TicketView;
import com.sap.sales_service.sale.infrastructure.output.domain.mapper.SaleTicketViewMapper;
import com.sap.sales_service.tickets.infrastructure.input.domain.port.TicketGatewayPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class SaleTicketAdapter implements FindTicketPort {

    private final TicketGatewayPort ticketGatewayPort;
    private final SaleTicketViewMapper ticketViewMapper;

    @Override
    public Optional<TicketView> findBySaleLineTicketId(UUID saleLineTicketId) {
        return ticketGatewayPort.findBySaleLineTicketId(saleLineTicketId)
                .map(ticketViewMapper::toView);
    }

    @Override
    public List<TicketView> findAllBySaleLineTicketIds(List<UUID> saleLineTicketIds) {
        var tickets = ticketGatewayPort.findBySaleLineTicketId(saleLineTicketIds);
        return tickets.stream()
                .map(ticketViewMapper::toView)
                .toList();
    }
}
