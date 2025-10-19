package com.sap.sales_service.sale.application.ouput;

import com.sap.sales_service.sale.domain.SaleLineSnack;

public interface SaveSaleLineSnackPort {
    SaleLineSnack save(SaleLineSnack saleLineSnack);
}
