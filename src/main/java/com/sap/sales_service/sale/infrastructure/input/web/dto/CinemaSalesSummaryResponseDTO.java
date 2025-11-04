package com.sap.sales_service.sale.infrastructure.input.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CinemaSalesSummaryResponseDTO(
        UUID cinemaId,
        String cinemaName,
        BigDecimal totalAmount,
        Long totalSales
) {
}
