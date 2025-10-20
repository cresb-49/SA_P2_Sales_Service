package com.sap.sales_service.tickets.application.usecases.markusedticket;

import com.sap.common_lib.common.enums.sale.SaleStatusType;
import com.sap.common_lib.common.enums.sale.TicketStatusType;
import com.sap.common_lib.exception.NotFoundException;
import com.sap.sales_service.tickets.application.input.MarkUsedTicketPort;
import com.sap.sales_service.tickets.application.output.FindingByFilterPort;
import com.sap.sales_service.tickets.application.output.FindingTicketPort;
import com.sap.sales_service.tickets.application.output.SaveTicketPort;
import com.sap.sales_service.tickets.domain.TicketFilter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class MarkUsedTicketCase implements MarkUsedTicketPort {

    private final FindingTicketPort findingTicketPort;
    private final FindingByFilterPort findingByFilterPort;
    private final SaveTicketPort saveTicketPort;

    @Override
    public void markTicketAsUsed(UUID ticketId) {
        findingTicketPort.findById(ticketId).orElseThrow(
                () -> new NotFoundException("El ticket con id " + ticketId + " no existe")
        );
        // Find by filter to ensure the sale as paid and ticket is valid
        var filter = TicketFilter.builder().ticketStatus(TicketStatusType.PURCHASED).saleStatus(SaleStatusType.PAID).build();
        var ticket = findingByFilterPort.findBySpecificIdAndFilter(filter, ticketId).orElseThrow(
                () -> new IllegalStateException("El ticket con id " + ticketId + " no es valido para ser usado, verifique que la venta este pagada y el ticket no haya sido usado previamente")
        );
        if (ticket.getId() != ticketId) {
            throw new RuntimeException("Error inesperado al marcar el ticket como usado");
        }
        ticket.markAsUsed();
        saveTicketPort.save(ticket);
    }
}
