package com.sap.sales_service.sale.infrastructure.output.jpa.adapter;

import com.sap.sales_service.sale.application.ouput.FindSaleLineSnackPort;
import com.sap.sales_service.sale.application.ouput.SaveSaleLineSnackPort;
import com.sap.sales_service.sale.domain.SaleLineSnack;

public class SaleLineSnackJpaAdapter implements FindSaleLineSnackPort, SaveSaleLineSnackPort {
    @Override
    public SaleLineSnack save(SaleLineSnack saleLineSnack) {
        return null;
    }
}
