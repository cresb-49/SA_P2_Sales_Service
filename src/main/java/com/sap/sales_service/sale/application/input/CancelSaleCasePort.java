package com.sap.sales_service.sale.application.input;

import java.util.UUID;

public interface CancelSaleCasePort {
    void cancelSaleById(UUID saleId);
}
