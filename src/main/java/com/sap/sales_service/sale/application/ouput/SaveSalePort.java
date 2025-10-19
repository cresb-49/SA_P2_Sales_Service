package com.sap.sales_service.sale.application.ouput;

import com.sap.sales_service.sale.domain.Sale;

public interface SaveSalePort {
    Sale save(Sale sale);
}
