package com.sap.sales_service.sale.application.input;

import com.sap.common_lib.common.enums.sale.TicketStatusType;

import java.util.UUID;

public interface UpdateTicketStateSalePort {
    void updateTicketState(UUID saleLineTicketId, TicketStatusType newStatus, String message);
}
