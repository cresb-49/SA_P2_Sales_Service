package com.sap.sales_service.tickets.application.output;

import com.sap.sales_service.tickets.domain.Ticket;
import com.sap.sales_service.tickets.domain.TicketFilter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface FindingByFilterPort {
    Optional<Ticket> findBySpecificIdAndFilter(TicketFilter filter, java.util.UUID specificId);

    List<Ticket> findByFilter(TicketFilter filter);

    Page<Ticket> findByFilterPaged(TicketFilter filter, int page);

}
