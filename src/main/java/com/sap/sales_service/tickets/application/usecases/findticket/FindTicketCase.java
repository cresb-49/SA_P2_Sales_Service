package com.sap.sales_service.tickets.application.usecases.findticket;

import com.sap.common_lib.exception.NotFoundException;
import com.sap.sales_service.tickets.application.input.FindTicketPort;
import com.sap.sales_service.tickets.application.output.FindingTicketPort;
import com.sap.sales_service.tickets.domain.Ticket;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class FindTicketCase implements FindTicketPort {

    private final FindingTicketPort findingTicketPort;

    @Override
    public Ticket findById(UUID id) {
        return findingTicketPort.findById(id).orElseThrow(
                () -> new NotFoundException("Ticket with id " + id + " not found")
        );
    }
}
