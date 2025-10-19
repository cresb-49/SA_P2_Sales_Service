package com.sap.sales_service.sale.application.factory;

import com.sap.sales_service.sale.application.ouput.FindSaleLineTicketPort;
import com.sap.sales_service.sale.application.ouput.FindTicketPort;
import com.sap.sales_service.sale.domain.SaleLineTicket;
import com.sap.sales_service.sale.domain.dtos.TicketView;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class SaleLineTicketFactory {

    private final FindSaleLineTicketPort findSaleLineTicketPort;
    private final FindTicketPort findTicketPort;

    public List<SaleLineTicket> saleLineTicketWithAllRelations(UUID saleId) {
        var saleLinesTickets = findSaleLineTicketPort.findAllBySaleId(saleId);
        var saleLineTicksId = saleLinesTickets.stream()
                .map(SaleLineTicket::getId)
                .toList();
        var tickets = findTicketPort.findAllBySaleLineTicketIds(saleLineTicksId);
        // Map tickets to with their IDs for easy access
        var ticketsMap = tickets.stream()
                .collect(java.util.stream.Collectors.toMap(TicketView::id, ticket -> ticket));
        // Set tickets to sale lines tickets
        saleLinesTickets.forEach(saleLineTicket -> {
            var ticket = ticketsMap.get(saleLineTicket.getId());
            saleLineTicket.setTicketView(ticket);
        });
        return saleLinesTickets;
    }

    public SaleLineTicket saleLineTicketWithAllRelations(SaleLineTicket saleLineTicket) {
        var ticket = findTicketPort.findBySaleLineTicketId(saleLineTicket.getId()).orElse(null);
        saleLineTicket.setTicketView(ticket);
        return saleLineTicket;
    }
}
