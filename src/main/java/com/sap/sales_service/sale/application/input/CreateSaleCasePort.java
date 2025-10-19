package com.sap.sales_service.sale.application.input;

import com.sap.sales_service.sale.application.usecases.create.dtos.CreateSaleDTO;
import com.sap.sales_service.sale.domain.Sale;

public interface CreateSaleCasePort {
    Sale createSale(CreateSaleDTO createSaleDTO);
}
