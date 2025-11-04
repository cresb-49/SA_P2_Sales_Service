package com.sap.sales_service.sale.infrastructure.input.web.dto;

import java.util.UUID;

public record CinemaSummaryResponseDTO(
        UUID id,
        String name
) {
}
