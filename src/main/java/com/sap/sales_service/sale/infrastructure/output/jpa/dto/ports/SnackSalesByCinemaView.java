package com.sap.sales_service.sale.infrastructure.output.jpa.dto.ports;

import java.math.BigDecimal;
import java.util.UUID;

public interface SnackSalesByCinemaView {
    UUID getCinemaId();
    UUID getSnackId();
    Long getTotalQuantity();
    BigDecimal getTotalAmount();
}
