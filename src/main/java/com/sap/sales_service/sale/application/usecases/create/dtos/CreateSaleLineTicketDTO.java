package com.sap.sales_service.sale.application.usecases.create.dtos;

import java.util.UUID;

public record CreateSaleLineTicketDTO(
        UUID cinemaFunctionId
) {
}
