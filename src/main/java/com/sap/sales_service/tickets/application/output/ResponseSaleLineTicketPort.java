package com.sap.sales_service.tickets.application.output;

import com.sap.common_lib.common.enums.sale.TicketStatusType;

import java.util.UUID;

public interface ResponseSaleLineTicketPort {
    void respondSaleLineTicket(UUID saleLineTicketId, TicketStatusType status, String message);
}
