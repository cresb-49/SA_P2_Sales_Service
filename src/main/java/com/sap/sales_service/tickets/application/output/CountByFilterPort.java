package com.sap.sales_service.tickets.application.output;

import com.sap.sales_service.tickets.domain.TicketFilter;

public interface CountByFilterPort {
    Long countByFilter(TicketFilter filter);
}
