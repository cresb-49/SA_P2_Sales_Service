package com.sap.sales_service.sale.domain.dtos.reports;

import com.sap.sales_service.sale.domain.dtos.CinemaView;

import java.math.BigDecimal;
import java.util.UUID;

public record CinemaSalesSummaryDTO(
        UUID cinemaId,
        BigDecimal totalAmount,
        Long totalSales,
        CinemaView cinema
) {
}
