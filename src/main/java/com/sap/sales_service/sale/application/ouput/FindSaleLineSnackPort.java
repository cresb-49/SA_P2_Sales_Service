package com.sap.sales_service.sale.application.ouput;

import com.sap.sales_service.sale.domain.SaleLineSnack;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FindSaleLineSnackPort {
    List<SaleLineSnack> findAllBySaleId(UUID saleId);

    Optional<SaleLineSnack> findById(UUID id);
}
