package com.sap.sales_service.sale.infrastructure.output.jpa.dto.ports;

import java.util.UUID;

public interface SnackSalesSummaryView {
    UUID getSnackId();
    Long getTotalQuantity();
}
