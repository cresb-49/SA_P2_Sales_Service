package com.sap.sales_service.sale.application.usecases.snacksalesreport.dto;

import com.sap.sales_service.sale.domain.dtos.CinemaView;
import com.sap.sales_service.sale.domain.dtos.reports.SnackSalesSummaryDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record SnackSalesReportDTO(
        List<SnackSalesSummaryDTO> snacks,
        Long totalQuantity,
        CinemaView cinema,
        UUID cinemaId,
        LocalDate from,
        LocalDate to
) {
}
