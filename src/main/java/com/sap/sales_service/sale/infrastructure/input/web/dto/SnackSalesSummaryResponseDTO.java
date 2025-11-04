package com.sap.sales_service.sale.infrastructure.input.web.dto;

import java.util.UUID;

public record SnackSalesSummaryResponseDTO(
        UUID snackId,
        String snackName,
        Long totalQuantity
) {
}
