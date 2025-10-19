package com.sap.sales_service.sale.application.input;

import java.util.UUID;

public interface ClaimTicketMoneySaleLineCasePort {
    void claimTicketMoneySaleLine(UUID saleLineId);
}
