package com.sap.sales_service.sale.application.usecases.create.dtos;

import java.util.List;
import java.util.UUID;

public record CreateSaleDTO(
        UUID clientId,
        UUID cinemaId,
        List<CreateSaleLineSnackDTO> snacks,
        List<CreateSaleLineTicketDTO> tickets
) {
}
