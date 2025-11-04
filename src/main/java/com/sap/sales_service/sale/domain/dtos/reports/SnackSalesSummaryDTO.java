package com.sap.sales_service.sale.domain.dtos.reports;

import com.sap.sales_service.sale.domain.dtos.SnackView;

import java.util.UUID;

public record SnackSalesSummaryDTO(
        UUID snackId,
        Long totalQuantity,
        SnackView snack
) {
}
