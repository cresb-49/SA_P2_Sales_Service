package com.sap.sales_service.sale.infrastructure.input.web.dto;

import com.sap.sales_service.sale.domain.dtos.CinemaView;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record SnackSalesReportResponseDTO(
        LocalDate from,
        LocalDate to,
        Long totalQuantity,
        CinemaView cinema,
        UUID cinemaId,
        List<SnackSalesSummaryResponseDTO> snacks
) {
}
