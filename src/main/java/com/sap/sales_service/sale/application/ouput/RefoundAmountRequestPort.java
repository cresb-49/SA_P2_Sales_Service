package com.sap.sales_service.sale.application.ouput;

import java.math.BigDecimal;
import java.util.UUID;

public interface RefoundAmountRequestPort {
    void requestRefoundAmount(BigDecimal amount, UUID customerId, String message);
}
