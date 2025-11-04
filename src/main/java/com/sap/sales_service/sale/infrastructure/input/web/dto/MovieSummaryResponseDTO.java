package com.sap.sales_service.sale.infrastructure.input.web.dto;

import java.util.UUID;

public record MovieSummaryResponseDTO(
        UUID id,
        String title
) {
}
