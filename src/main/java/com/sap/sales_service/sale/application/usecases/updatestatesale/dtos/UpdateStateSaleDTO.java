package com.sap.sales_service.sale.application.usecases.updatestatesale.dtos;

import java.util.UUID;

public record UpdateStateSaleDTO(
        UUID saleId,
        boolean paid,
        String message
) {
}
