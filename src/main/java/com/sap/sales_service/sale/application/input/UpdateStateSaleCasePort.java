package com.sap.sales_service.sale.application.input;

import com.sap.sales_service.sale.application.usecases.updatestatesale.dtos.UpdateStateSaleDTO;

public interface UpdateStateSaleCasePort {
    void updateStateSale(UpdateStateSaleDTO updateStateSaleDTO);
}
