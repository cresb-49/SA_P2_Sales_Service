package com.sap.sales_service.sale.application.ouput;

import java.math.BigDecimal;
import java.util.UUID;

public interface SendPaidRequestPort {
    void sendPaidRequest(UUID userId, UUID cinemaId, UUID saleId, BigDecimal amount);
}
