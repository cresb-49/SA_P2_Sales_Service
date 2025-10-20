package com.sap.sales_service.sale.application.ouput;

import java.util.UUID;

public interface FindUserPort {
    boolean existsById(UUID userId);
}
